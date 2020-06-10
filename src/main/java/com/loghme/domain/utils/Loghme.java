package com.loghme.domain.utils;

import com.loghme.domain.schedulers.CouriersScheduler;
import com.loghme.domain.utils.exceptions.*;
import com.loghme.repository.LoghmeRepository;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.service.DTO.OrderDTO;
import com.loghme.service.DTO.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

public class Loghme
{
    private static Loghme singleApp = null;
    private LoghmeRepository loghmeRepository = LoghmeRepository.getInstance();
    private long partyStartTime;

    public static Loghme getInstance() {
        if (singleApp == null)
            singleApp = new Loghme();

        return singleApp;
    }

    public void setPartyStartTime() {
        partyStartTime = System.currentTimeMillis();
    }

    public long getPartyStartTime() { return partyStartTime; }

    public void changeCart(String username, String restaurantId, String foodName, int count, boolean isPartyFood) throws FoodFromOtherRestaurantInCartExp, ExtraFoodPartyExp, NotEnoughFoodToDelete, SQLException {
        String currentOrderRestaurantId = loghmeRepository.getCurrentOrderRestaurantId(username);
        if (currentOrderRestaurantId == null || currentOrderRestaurantId.equals(restaurantId)) {
            if (isPartyFood)
                loghmeRepository.changeCurrentOrder(username, foodName, restaurantId, count, "party");
            else
                loghmeRepository.changeCurrentOrder(username, foodName, restaurantId, count, "normal");
        }
        else
            throw new FoodFromOtherRestaurantInCartExp();
    }

    public void finalizeOrder(String username) throws NotEnoughCreditExp, RestaurantNotFoundExp, SQLException {
        int orderId = loghmeRepository.finalizeOrder(username);
        Location location = loghmeRepository.getOrderRestaurantLocation(orderId);
        Timer timer = new Timer();
        TimerTask task = new CouriersScheduler(location, orderId);
        timer.schedule(task, 0, 3000);
    }

    public void addCredit(String username, String json) throws JSONException, NotEnoughCreditExp {
        JSONObject obj = new JSONObject(json);
        loghmeRepository.changeCredit(username, obj.getInt("credit"));
    }

    public int getUserCredit(String username) throws SQLException {
        return loghmeRepository.getCredit(username);
    }

    public UserDTO getUserDTO(String username) throws SQLException {
        return loghmeRepository.getUserDTO(username);
    }

    public ArrayList<OrderDTO> getUserOrders(String username) throws SQLException {
        return loghmeRepository.getOrders(username);
    }

    public ArrayList<FoodDTO> getCurrentOrderFoods(String username) throws SQLException {
        return loghmeRepository.getCurrentOrderFoods(username);
    }

    public void addPartyRestaurants(ArrayList<Restaurant> partyRestaurants) throws SQLException {
        loghmeRepository.invalidPrevPartyFoods();
        for (Restaurant restaurant: partyRestaurants) {
            loghmeRepository.addRestaurant(restaurant.getId(), restaurant.getName(), restaurant.getLogo(), restaurant.getLocation().getX(), restaurant.getLocation().getY());
            for (PartyFood partyFood: restaurant.getPartyFoods()) {
                int foodId = loghmeRepository.addFood(restaurant.getId(), partyFood.getName(), partyFood.getDescription(), partyFood.getPopularity(), partyFood.getImage(), partyFood.getPrice(), partyFood.getCount());
                loghmeRepository.addPartyFood(restaurant.getId(), foodId, partyFood.getNewPrice(), partyFood.getCount());

            }
        }
    }

    public String hashPassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public static String createJWT(String id, String issuer, long ttlMillis) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("loghme");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signingKey);

        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("loghme"))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    public String addUser(String json) throws JSONException, DuplicateEmail, SQLException {
        JSONObject obj = new JSONObject(json);
        loghmeRepository.addUser(obj.getString("firstName"), obj.getString("lastName"), obj.getString("email"), hashPassword(obj.getString("password")));
        return createJWT(obj.getString("email"), obj.getString("issuer"), 86400000);
    }

    public String loginUser(String json) throws JSONException, UserNotFoundExp, WrongPasswordExp {
        JSONObject obj = new JSONObject(json);
        if (obj.getBoolean("authWithGoogle"))
            return createJWT(obj.getString("email"), obj.getString("issuer"), 86400000);
        else if (loghmeRepository.authenticate(obj.getString("email"), hashPassword(obj.getString("password"))) == 1)
            return createJWT(obj.getString("email"), obj.getString("issuer"), 86400000);
        else
            throw new WrongPasswordExp();
    }
}