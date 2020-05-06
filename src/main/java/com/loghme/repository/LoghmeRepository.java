package com.loghme.repository;

import com.loghme.domain.utils.Food;
import com.loghme.domain.utils.Location;
import com.loghme.domain.utils.PartyFood;
import com.loghme.domain.utils.exceptions.ExtraFoodPartyExp;
import com.loghme.domain.utils.exceptions.NotEnoughCreditExp;
import com.loghme.domain.utils.exceptions.NotEnoughFoodToDelete;
import com.loghme.domain.utils.exceptions.RestaurantNotFoundExp;
import com.loghme.repository.DAO.FoodDAO;
import com.loghme.repository.DAO.PartyFoodDAO;
import com.loghme.repository.DAO.RestaurantDAO;
import com.loghme.service.DTO.FoodDTO;
import com.loghme.service.DTO.OrderDTO;
import com.loghme.service.DTO.UserDTO;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class LoghmeRepository {
    private static LoghmeRepository instance;
    public static final int MYSQL_DUPLICATE_PK = 1062;

    ComboPooledDataSource dataSource;

    private LoghmeRepository() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/loghme6?useSSL=false");
        dataSource.setUser("root");
        dataSource.setPassword("Taha1378");

        dataSource.setInitialPoolSize(5);
        dataSource.setMinPoolSize(5);
        dataSource.setAcquireIncrement(5);
        dataSource.setMaxPoolSize(100);
        dataSource.setMaxStatements(200);
    }

    public static LoghmeRepository getInstance() {
        if (instance == null)
            instance = new LoghmeRepository();
        return instance;
    }

    public void loginUser(String username, String password) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Users where username = \"" + username + "\" and password = \"" + password +"\"");
            if (result.next()) {
                String phoneNumber = result.getString("phoneNumber");
                //same way for other attributes
            }
            statement.close();
            connection.close();
            //exception not handled
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RestaurantDAO> getRestaurants() {
        ArrayList<RestaurantDAO> restaurants = new ArrayList<RestaurantDAO>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Restaurants");
            while (result.next()) {
                RestaurantDAO restaurantDao = new RestaurantDAO();
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
                restaurants.add(restaurantDao);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    public ArrayList<RestaurantDAO> getRestaurantsOnLevel(int numOfRestaurant) {
        ArrayList<RestaurantDAO> restaurants = new ArrayList<RestaurantDAO>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Restaurants limit " + numOfRestaurant + ";");
            while (result.next()) {
                RestaurantDAO restaurantDao = new RestaurantDAO();
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
                restaurants.add(restaurantDao);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    public ArrayList<RestaurantDAO> getSearchedRestaurants(String restaurantName, String foodName){
        ArrayList<RestaurantDAO> restaurants = new ArrayList<RestaurantDAO>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            String sql = "select distinct R.* from Restaurants R, Foods F, Menu M where (F.name like \"%" + foodName + "%\" or R.name like \"%" + restaurantName + "%\") and R.id = M.restaurantId and F.id = M.foodId";
            if (restaurantName == "" && foodName != "")
                sql = "select distinct R.* from Restaurants R, Foods F, Menu M where (F.name like \"%" + foodName + "%\") and R.id = M.restaurantId and F.id = M.foodId";
            else if (restaurantName != "" && foodName == "")
                sql = "select distinct R.* from Restaurants R, Foods F, Menu M where (R.name like \"%" + restaurantName + "%\") and R.id = M.restaurantId and F.id = M.foodId";
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                RestaurantDAO restaurantDao = new RestaurantDAO();
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
                restaurants.add(restaurantDao);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    public void addRestaurant(String id, String name, String logo, float x, float y) throws SQLException {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(
                    "insert into Restaurants (id, name, logoUrl, x, y) values (?, ?, ?, ?, ?)");
            pStatement.setString(1, id);
            pStatement.setString(2, name);
            pStatement.setString(3, logo);
            pStatement.setFloat(4, x);
            pStatement.setFloat(5, y);
            pStatement.executeUpdate();
            pStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            if(e.getErrorCode() == MYSQL_DUPLICATE_PK ) {
                connection = dataSource.getConnection();
                PreparedStatement pStatement = connection.prepareStatement(
                        "UPDATE Restaurants " +
                                "SET name=?, logoUrl=?, x=?, y=? " +
                                "WHERE id = ?;");
                pStatement.setString(1, name);
                pStatement.setString(2, logo);
                pStatement.setFloat(3, x);
                pStatement.setFloat(4, y);
                pStatement.setString(5, id);
                pStatement.executeUpdate();
                pStatement.close();
                connection.close();
            }
        }
    }

    public int addFood(String restaurantId, String name, String description, float popularity, String imageUrl, int price, int count) {
        Connection connection;
        int foodId = 0;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select F.id from Foods F, Menu M where M.restaurantId = \"" + restaurantId + "\" and F.name = \"" + name + "\" and M.foodId = F.id");
            if (result.next()) { //food already exits -> update Foods
                foodId = result.getInt("id");
                PreparedStatement pStatement = connection.prepareStatement(
                        "update Foods set description = ?, popularity = ?, imageUrl = ?, price = ?, count = ? where id = ?");
                pStatement.setString(1, description);
                pStatement.setFloat(2, popularity);
                pStatement.setString(3, imageUrl);
                pStatement.setInt(4, price);
                pStatement.setInt(5, count);
                pStatement.setInt(6, foodId);
                pStatement.executeUpdate();
                pStatement.close();
            }
            else { //new food -> insert into Foods and Menu
                PreparedStatement pStatement = connection.prepareStatement(
                        "insert into Foods (name, description, popularity, imageUrl, price, count) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pStatement.setString(1, name);
                pStatement.setString(2, description);
                pStatement.setFloat(3, popularity);
                pStatement.setString(4, imageUrl);
                pStatement.setInt(5, price);
                pStatement.setInt(6, count);
                pStatement.executeUpdate();
                ResultSet rs = pStatement.getGeneratedKeys();

                if(rs.next())
                    foodId = rs.getInt(1);
                rs.close();
                pStatement.close();

                PreparedStatement pStatementMenu = connection.prepareStatement(
                        "insert into Menu (restaurantId, foodId) values (?, ?)");
                pStatementMenu.setString(1, restaurantId);
                pStatementMenu.setInt(2, foodId);
                pStatementMenu.executeUpdate();
                pStatementMenu.close();
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return foodId;
    }

    public void invalidPrevPartyFoods() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            String invalid = "0";
            String valid = "1";
            PreparedStatement pStatement = connection.prepareStatement(
                    "update PartyFoods set valid = ? where valid = ?");
            pStatement.setString(1, invalid);
            pStatement.setString(2, valid);
            pStatement.executeUpdate();
            pStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPartyFood(String restaurantId, int foodId, int newPrice, int count) {
        Connection connection;
        int partyFoodId = 0;
        String valid = "1";
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select PF.id from PartyFoods PF, PartyMenu PM where PM.restaurantId = \"" + restaurantId + "\" and PF.foodId = " + foodId + " and PM.partyFoodId = PF.id");
            if (result.next()) { //partyFood already exits -> update PartyFoods
                partyFoodId = result.getInt("id");
                PreparedStatement pStatement = connection.prepareStatement(
                        "update PartyFoods set newPrice = ?, count = ?, valid = ? where id = ?");

                pStatement.setInt(1, newPrice);
                pStatement.setInt(2, count);
                pStatement.setString(3, valid);
                pStatement.setInt(4, partyFoodId);
                pStatement.executeUpdate();
                pStatement.close();
            } else { //new food -> insert into PartyFoods and Menu

                PreparedStatement pStatement = connection.prepareStatement(
                        "insert into PartyFoods (foodId, newPrice, count, valid) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pStatement.setInt(1, foodId);
                pStatement.setInt(2, newPrice);
                pStatement.setInt(3, count);
                pStatement.setString(4, valid);
                pStatement.executeUpdate();
                ResultSet rs = pStatement.getGeneratedKeys();

                if (rs.next())
                    partyFoodId = rs.getInt(1);
                rs.close();
                pStatement.close();

                PreparedStatement pStatementMenu = connection.prepareStatement(
                        "insert into PartyMenu (restaurantId, partyFoodId) values (?, ?)");
                pStatementMenu.setString(1, restaurantId);
                pStatementMenu.setInt(2, partyFoodId);
                pStatementMenu.executeUpdate();
                pStatementMenu.close();
            }
            result.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RestaurantDAO getRestaurantById(String restaurantId) {
        RestaurantDAO restaurantDao = new RestaurantDAO();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Restaurants where id = \"" + restaurantId  +"\"");
            if (result.next()) {
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurantDao;
    }

    public RestaurantDAO getRestaurantByPartyFoodId(int partyFoodId) {
        RestaurantDAO restaurantDao = new RestaurantDAO();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select R.* from Restaurants R, PartyMenu PM where PM.partyFoodId = \"" + partyFoodId  +"\" and PM.restaurantId = R.id");
            if (result.next()) {
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurantDao;
    }

    public ArrayList<FoodDAO> getRestaurantFoods(String restaurantId) {
        ArrayList<FoodDAO> foods = new ArrayList<FoodDAO>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select F.* from Foods F, Menu M where M.restaurantId = \"" + restaurantId  +"\" and M.foodId = F.id");
            while (result.next()) {
                FoodDAO foodDAO = new FoodDAO();
                foodDAO.setId(result.getInt("id"));
                foodDAO.setName(result.getString("name"));
                foodDAO.setDescription(result.getString("description"));
                foodDAO.setPopularity(result.getFloat("popularity"));
                foodDAO.setImageUrl(result.getString("imageUrl"));
                foodDAO.setPrice(result.getInt("price"));
                foodDAO.setCount(result.getInt("count"));
                foods.add(foodDAO);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return foods;
    }

    public ArrayList<PartyFoodDAO> getValidPartyFoods () {
        ArrayList<PartyFoodDAO> partyFoods = new ArrayList<PartyFoodDAO>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select PF.id as pid, PF.newPrice, PF.count as pCount, PF.valid, F.* from PartyFoods PF,Foods F, PartyMenu PM where PF.valid = \"1\" and PF.foodId = F.id and PM.partyFoodId = PF.id");
            while (result.next()) {
                PartyFoodDAO partyFoodDAO = new PartyFoodDAO();
                partyFoodDAO.setId(result.getInt("pid"));
                partyFoodDAO.setName(result.getString("name"));
                partyFoodDAO.setDescription(result.getString("description"));
                partyFoodDAO.setPopularity(result.getFloat("popularity"));
                partyFoodDAO.setImageUrl(result.getString("imageUrl"));
                partyFoodDAO.setPrice(result.getInt("price"));
                partyFoodDAO.setCount(result.getInt("pCount"));
                partyFoodDAO.setNewPrice(result.getInt("newPrice"));
                partyFoodDAO.setValid(result.getString("valid"));
                partyFoods.add(partyFoodDAO);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return partyFoods;
    }

    public int getCredit(String username) {
        Connection connection;
        int credit = 0;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT credit FROM Users WHERE username=\"" + username + "\"");
            if (result.next())
                credit = result.getInt("credit");
            else
                throw new SQLException();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return credit;
    }

    public void changeCredit(String username, int addingCredit) throws NotEnoughCreditExp {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int newCredit = getCredit(username) + addingCredit;
            if (newCredit < 0)
                throw new NotEnoughCreditExp();
            statement.executeUpdate("UPDATE Users SET credit=" + newCredit + " WHERE username=\"" + username + "\"");
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getFoodId(String foodName, String restaurantId) {
        Connection connection;
        int foodId = -1;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT F.id FROM Menu M, Foods F WHERE M.restaurantId=\"" + restaurantId + "\" and F.name=\"" + foodName + "\" and F.id=M.foodId ");
            if (result.next())
                foodId = result.getInt("id");
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return foodId;
    }

    public int getPartyFoodId(String foodName, String restaurantId) {
        Connection connection;
        int partyfoodId = -1;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int foodId = getFoodId(foodName, restaurantId);
            ResultSet result = statement.executeQuery("SELECT F.id FROM PartyMenu M, PartyFoods F WHERE M.restaurantId=\"" + restaurantId + "\" and F.foodId=" + foodId + " and F.id=M.partyfoodId ");
            if (result.next())
                partyfoodId = result.getInt("id");
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return partyfoodId;
    }

    public int addOrder(String username, String restaurantId, String status, HashMap<Food, Integer> foods, HashMap<PartyFood, Integer> partyFoods) {
        Connection connection;
        int orderId = 0;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(
                    "insert into Orders (username, restaurantId, status, registerTime) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            Date date = new Date();
            Object param = new java.sql.Timestamp(date.getTime());
            pStatement.setString(1, username);
            pStatement.setString(2, restaurantId);
            pStatement.setString(3, status);
            pStatement.setObject(4, param);
            pStatement.executeUpdate();

            ResultSet rs = pStatement.getGeneratedKeys();
            if(rs.next())
                orderId = rs.getInt(1);
            rs.close();
            pStatement.close();

            if (foods == null && partyFoods == null)
                return orderId;

            PreparedStatement orderRowStatement = connection.prepareStatement(
                    "insert into OrderRows (orderId, foodId, partyFoodId, count, foodType) values (?, ?, ?, ?, ?)");
            for (Map.Entry<Food, Integer> entry: foods.entrySet()) {
                orderRowStatement.clearParameters();
                int foodId = getFoodId(entry.getKey().getName(), restaurantId);
                if (foodId == -1) {
                    throw new SQLException();
                }
                orderRowStatement.setInt(1, orderId);
                orderRowStatement.setInt(2, foodId);
                orderRowStatement.setString(3, null);
                orderRowStatement.setInt(4, entry.getValue());
                orderRowStatement.setString(5, "normal");
                orderRowStatement.addBatch();
            }
            for (Map.Entry<PartyFood, Integer> entry: partyFoods.entrySet()) {
                orderRowStatement.clearParameters();
                int foodId = getPartyFoodId(entry.getKey().getName(), restaurantId);
                if (foodId == -1) {
                    throw new SQLException();
                }
                orderRowStatement.setInt(1, orderId);
                orderRowStatement.setString(2, null);
                orderRowStatement.setInt(3, foodId);
                orderRowStatement.setInt(4, entry.getValue());
                orderRowStatement.setString(5, "party");
                orderRowStatement.addBatch();
            }
            orderRowStatement.executeBatch();
            orderRowStatement.close();

            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    public void addUser(String id, String name, String phoneNumber, String email, int credit, String password) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(
                    "insert into Users (name, email, credit, phoneNumber, username, password) values (?, ?, ?, ?, ?, ?)");
            pStatement.setString(1, name);
            pStatement.setString(2, email);
            pStatement.setInt(3, credit);
            pStatement.setString(4, phoneNumber);
            pStatement.setString(5, id);
            pStatement.setString(6, password);
            pStatement.executeUpdate();
            pStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            if(e.getErrorCode() != MYSQL_DUPLICATE_PK )
                e.printStackTrace();
        }
    }

    public UserDTO getUserDTO(String username) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT * FROM Users WHERE username=\"" + username + "\"");
            if (result.next()) {
                String name = result.getString("name");
                String phoneNumber = result.getString("phoneNumber");
                String email = result.getString("email");
                int credit = result.getInt("credit");
                return new UserDTO(username, name, phoneNumber, email, credit);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FoodDTO getFoodDTOById(int foodId, int partyFoodId, String foodType) {
        FoodDTO foodDTO = new FoodDTO();
        Connection connection;
        if (foodType.equals("normal")) {
            try {
                connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM Foods WHERE  id=" + foodId);
                if (result.next()) {
                    foodDTO.setName(result.getString("name"));
                    foodDTO.setDescription(result.getString("description"));
                    foodDTO.setImage(result.getString("imageUrl"));
                    foodDTO.setPopularity(result.getFloat("popularity"));
                    foodDTO.setPrice(result.getInt("price"));
                }
                else
                    throw new SQLException();
                result.close();
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                int newFoodId;
                ResultSet result = statement.executeQuery("SELECT * FROM PartyFoods WHERE id=" + partyFoodId);
                if (result.next()) {
                    foodDTO.setPrice(result.getInt("newPrice"));
                    newFoodId = result.getInt("foodId");
                }
                else
                    throw new SQLException();
                result.close();
                statement.close();

                Statement statement2 = connection.createStatement();
                ResultSet result2 = statement2.executeQuery("SELECT * FROM Foods WHERE id=" + newFoodId);
                if (result2.next()) {
                    foodDTO.setPopularity(result2.getFloat("popularity"));
                    foodDTO.setImage(result2.getString("imageUrl"));
                    foodDTO.setDescription(result2.getString("description"));
                    foodDTO.setName(result2.getString("name"));
                }
                else
                    throw new SQLException();
                result.close();
                statement2.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return foodDTO;
    }

    public ArrayList<FoodDTO> getFoodsByOrderId(int orderId) {
        ArrayList<FoodDTO> foodDTOS = new ArrayList<>();

        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from OrderRows where orderId=" + orderId);
            while (result.next()) {
                FoodDTO foodDTO = new FoodDTO();
                foodDTO.setCount(result.getInt("count"));
                FoodDTO food = getFoodDTOById(result.getInt("foodId"), result.getInt("partyFoodId"), result.getString("foodType"));
                foodDTO.setPrice(food.getPrice());
                foodDTO.setName(food.getName());
                foodDTOS.add(foodDTO);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return foodDTOS;
    }

    public ArrayList<OrderDTO> getOrders(String username) {
        ArrayList<OrderDTO> orders = new ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from Orders where username=\"" + username + "\"");
            while (result.next()) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setRestaurantName(getRestaurantById(result.getString("restaurantId")).getName());
                orderDTO.setStatus(result.getString("status"));
                orderDTO.setFoods(getFoodsByOrderId(result.getInt("id")));
                orders.add(orderDTO);
            }
            result.close();
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void updateOrderStatus(String status, int orderId) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("UPDATE Orders SET status=\"" + status + "\" WHERE id=" + orderId);
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentOrderRestaurantId(String username) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT restaurantId FROM Orders WHERE status=\"notFinalized\" and username=\"" + username + "\"");
            if (result.next())
                return result.getString("restaurantId");
            else
                return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPartyFoodCount(String restaurantId, String foodName) {
        Connection connection;
        try {
            int foodId = getPartyFoodId(foodName, restaurantId);
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT count FROM PartyFoods WHERE id=" + foodId);
            if (result.next())
                return result.getInt("count");
            else
                throw new SQLException();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getOrderFoodCount(int orderId, int foodId, String foodType) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result;
            if (foodType.equals("normal")) {
                result = statement.executeQuery(
                        "SELECT R.count FROM OrderRows R, Orders O WHERE O.id=" + orderId + " and R.orderId=O.id and R.foodId=" + foodId);
            }
            else {
                result = statement.executeQuery(
                        "SELECT R.count FROM OrderRows R, Orders O WHERE O.id=" + orderId + " and R.orderId=O.id and R.partyFoodId=" + foodId);
            }
            if (result.next())
                return result.getInt("count");
            else
                return -1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getCurrentOrderId(String username) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT id FROM Orders WHERE username=\"" + username + "\" and status=\"notFinalized\"");
            if (result.next())
                return result.getInt("id");
            else
                return -1;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void changeCurrentOrder(String username, String foodName, String restaurantId, int count, String foodType) throws NotEnoughFoodToDelete, ExtraFoodPartyExp {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            int foodId;
            if (foodType.equals("normal"))
                foodId = getFoodId(foodName, restaurantId);
            else
                foodId = getPartyFoodId(foodName, restaurantId);

            int orderId = getCurrentOrderId(username);
            if (orderId == -1) {
                orderId = addOrder(username, restaurantId, "notFinalized", null, null);
            }

            int prevCount = getOrderFoodCount(orderId, foodId, foodType);
            if (prevCount != -1) {
                int newCount = prevCount + count;
                if (newCount < 0)
                    throw new NotEnoughFoodToDelete();
                else if (foodType.equals("party") && newCount > getPartyFoodCount(restaurantId, foodName))
                    throw new ExtraFoodPartyExp();

                if (foodType.equals("normal"))
                    statement.executeUpdate("UPDATE OrderRows SET count=" + newCount + " WHERE orderId=" + orderId + " and foodId=" + foodId);
                else
                    statement.executeUpdate("UPDATE OrderRows SET count=" + newCount + " WHERE orderId=" + orderId + " and partyFoodId=" + foodId);
            }
            else {
                if (foodType.equals("normal"))
                    statement.executeUpdate("INSERT INTO OrderRows (orderId, foodId, count, foodType) values (" + orderId + ", " + foodId + ", " + count + ", \"normal\")");
                else
                    statement.executeUpdate("INSERT INTO OrderRows (orderId, partyFoodId, count, foodType) values (" + orderId + ", " + foodId + ", " + count + ", \"party\")");
            }
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FoodDTO> getCurrentOrderFoods(String username) {
        int orderId = getCurrentOrderId(username);
        return getFoodsByOrderId(orderId);
    }

    public int getCartPrice(ArrayList<FoodDTO> foodDTOS) {
        int price = 0;
        for (FoodDTO foodDTO: foodDTOS) {
            price += foodDTO.getPrice() * foodDTO.getCount();
        }
        return price;
    }

    public void removeCurrentOrder(String username) {
        int orderId = getCurrentOrderId(username);
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM Orders WHERE id=" + orderId);
            statement.executeUpdate("DELETE FROM OrderRows WHERE orderId=" + orderId);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reducePartyFoods(int orderId) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM OrderRows WHERE orderId=" + orderId + " and foodType=\"party\"");
            while (result.next()) {
                Statement statement2 = connection.createStatement();
                statement2.executeUpdate("UPDATE PartyFoods SET count = count-" + result.getInt("count") + " WHERE id=" + result.getInt("partyFoodId"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int finalizeOrder(String username) throws NotEnoughCreditExp {
        int orderId = getCurrentOrderId(username);
        ArrayList<FoodDTO> foodDTOS = getFoodsByOrderId(orderId);
        int cartPrice = getCartPrice(foodDTOS);
        try {
            changeCredit(username, -cartPrice);
        }
        catch (NotEnoughCreditExp e) {
            removeCurrentOrder(username);
            throw new NotEnoughCreditExp();
        }
        reducePartyFoods(orderId);
        updateOrderStatus("searching", orderId);
        return orderId;
    }

    public Location getOrderRestaurantLocation(int orderId) throws RestaurantNotFoundExp {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "SELECT R.* FROM Restaurants R, Orders O WHERE O.id=" + orderId + " and O.restaurantId=R.id");
            if (result.next()) {
                float x = result.getFloat("x");
                float y = result.getFloat("y");
                return new Location(x, y);
            }
            else
                throw new SQLException();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RestaurantNotFoundExp();
    }
}
