package com.loghme.repository;

import com.loghme.domain.utils.Food;
import com.loghme.domain.utils.Location;
import com.loghme.domain.utils.PartyFood;
import com.loghme.domain.utils.exceptions.*;
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
        dataSource.setJdbcUrl("jdbc:mysql://loghme-app-mysql:3306/loghme6?useSSL=false");
        dataSource.setUser("loghme2");
        dataSource.setPassword("loghme2");

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

    public void createTables() {
        Connection connection;
        Statement statement;
        try {
            connection = dataSource.getConnection();
            statement =  connection.createStatement();

            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables;
            tables = dbm.getTables(null, null, "Users", null);
            if (!tables.next()) {
                statement.executeUpdate("create table Users (\n" +
                        "    firstName char(100),\n" +
                        "    lastName char(100),\n" +
                        "    email char(100),\n" +
                        "    credit integer,\n" +
                        "    password char(100),\n" +
                        "    primary key (email)\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "Restaurants", null);
            if (!tables.next()) {
                statement.executeUpdate("create table Restaurants (\n" +
                        "    id char(100),\n" +
                        "    name char(100),\n" +
                        "    logoUrl char(255),\n" +
                        "    x float,\n" +
                        "    y float,\n" +
                        "    primary key (id)\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "Foods", null);
            if (!tables.next()) {
                statement.executeUpdate("create table Foods (\n" +
                        "    id integer NOT NULL AUTO_INCREMENT,\n" +
                        "    name char(100),\n" +
                        "    description text,\n" +
                        "    popularity float,\n" +
                        "    imageUrl char(255),\n" +
                        "    price integer,\n" +
                        "    count integer,\n" +
                        "    primary key (id)\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "PartyFoods", null);
            if (!tables.next()) {
                statement.executeUpdate("create table PartyFoods (\n" +
                        "    id integer NOT NULL AUTO_INCREMENT,\n" +
                        "    foodId integer,\n" +
                        "    newPrice integer,\n" +
                        "    count integer,\n" +
                        "    valid char(1),\n" +
                        "    primary key (id),\n" +
                        "    foreign key (foodId) references Foods(id) on delete cascade\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "Menu", null);
            if (!tables.next()) {
                statement.executeUpdate("create table Menu (\n" +
                        "    restaurantId char(100),\n" +
                        "    foodId integer,\n" +
                        "    primary key (restaurantId, foodId),\n" +
                        "    foreign key (restaurantId) references Restaurants(id) on delete cascade,\n" +
                        "    foreign key (foodId) references Foods(id) on delete cascade\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "PartyMenu", null);
            if (!tables.next()) {
                statement.executeUpdate("create table PartyMenu (\n" +
                        "    restaurantId char(100),\n" +
                        "    partyFoodId integer,\n" +
                        "    primary key (restaurantId, partyFoodId),\n" +
                        "    foreign key (restaurantId) references Restaurants(id) on delete cascade,\n" +
                        "    foreign key (partyFoodId) references PartyFoods(id) on delete cascade\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "Orders", null);
            if (!tables.next()) {
                statement.executeUpdate("create table Orders (\n" +
                        "    id integer NOT NULL AUTO_INCREMENT,\n" +
                        "    username char(50),\n" +
                        "    restaurantId char(100),\n" +
                        "    status ENUM ('searching', 'delivering', 'done', 'notFinalized'),\n" +
                        "    registerTime DATETIME,\n" +
                        "    primary key (id),\n" +
                        "    foreign key (username) references Users(email) on delete cascade,\n" +
                        "    foreign key (restaurantId) references Restaurants(id) on delete cascade\n" +
                        ");");
            }

            tables = dbm.getTables(null, null, "OrderRows", null);
            if (!tables.next()) {
                statement.executeUpdate("create table OrderRows (\n" +
                        "    id integer NOT NULL AUTO_INCREMENT,\n" +
                        "    orderId integer,\n" +
                        "    foodId integer NULL,\n" +
                        "    partyFoodId integer NULL,\n" +
                        "    count integer,\n" +
                        "    foodType ENUM ('normal', 'party'),\n" +
                        "    primary key (id),\n" +
                        "    foreign key (orderId) references Orders(id) on delete cascade,\n" +
                        "    foreign key (foodId) references Foods(id) on delete cascade,\n" +
                        "    foreign key (partyFoodId) references PartyFoods(id) on delete cascade\n" +
                        ");");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int authenticate(String email, String password) throws UserNotFoundExp {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(
                    "select * from Users where email=?");
            pStatement.setString(1, email);
            ResultSet rs = pStatement.executeQuery();
            if (rs.next()) {
                if (rs.getString("password").equals(password))
                    return 1;
                else
                    return -1;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new UserNotFoundExp();
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
        ArrayList<RestaurantDAO> restaurants = new ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            PreparedStatement pStatement;
            if (restaurantName.equals("") && !foodName.equals("")) {
                pStatement = connection.prepareStatement("select distinct R.* from Restaurants R, Foods F, Menu M " +
                        "where F.name like ? and R.id = M.restaurantId and F.id = M.foodId");
                pStatement.setString(1, "%" + foodName + "%");
            }
            else if (!restaurantName.equals("") && foodName.equals("")) {
                pStatement = connection.prepareStatement("select distinct R.* from Restaurants R, Foods F, Menu M " +
                        "where R.name like ? and R.id = M.restaurantId and F.id = M.foodId");
                pStatement.setString(1, "%" + restaurantName + "%");
            }
            else {
                pStatement = connection.prepareStatement("select distinct R.* from Restaurants R, Foods F, Menu M " +
                        "where (F.name like ? or R.name like ?) and R.id = M.restaurantId and F.id = M.foodId");
                pStatement.setString(1, "%" + foodName + "%");
                pStatement.setString(2, "%" + restaurantName + "%");
            }
            ResultSet result = pStatement.executeQuery();
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
            PreparedStatement preStatement = connection.prepareStatement("select F.id from Foods F, Menu M " +
                    "where M.restaurantId = ? and F.name = ? and M.foodId = F.id");
            preStatement.setString(1, restaurantId);
            preStatement.setString(2, name);
            ResultSet result = preStatement.executeQuery();
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
            preStatement.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("select PF.id from PartyFoods PF, PartyMenu PM where " +
                    "PM.restaurantId = ? and PF.foodId = ? and PM.partyFoodId = PF.id");
            preparedStatement.setString(1, restaurantId);
            preparedStatement.setInt(2, foodId);
            ResultSet result = preparedStatement.executeQuery();
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
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RestaurantDAO getRestaurantById(String restaurantId) {
        RestaurantDAO restaurantDao = new RestaurantDAO();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from Restaurants where id = ?");
            preparedStatement.setString(1, restaurantId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
            }
            result.close();
            preparedStatement.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("select R.* from Restaurants R, PartyMenu PM where PM.partyFoodId = ? and PM.restaurantId = R.id");
            preparedStatement.setInt(1, partyFoodId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                restaurantDao.setId(result.getString("id"));
                restaurantDao.setName(result.getString("name"));
                restaurantDao.setLogoUrl(result.getString("logoUrl"));
                restaurantDao.setX(result.getFloat("x"));
                restaurantDao.setY(result.getFloat("y"));
            }
            result.close();
            preparedStatement.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("select F.* from Foods F, Menu M where M.restaurantId = ? and M.foodId = F.id");
            preparedStatement.setString(1, restaurantId);
            ResultSet result = preparedStatement.executeQuery();
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
            preparedStatement.close();
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

    public int getCredit(String email) {
        Connection connection;
        int credit = 0;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT credit FROM Users WHERE email=?");
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
                credit = result.getInt("credit");
            else
                throw new SQLException();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return credit;
    }

    public void changeCredit(String email, int addingCredit) throws NotEnoughCreditExp {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement;
            int newCredit = getCredit(email) + addingCredit;
            if (newCredit < 0)
                throw new NotEnoughCreditExp();
            preparedStatement = connection.prepareStatement("UPDATE Users SET credit=? WHERE email=?");
            preparedStatement.setInt(1, newCredit);
            preparedStatement.setString(2, email);
            preparedStatement.executeUpdate();
            preparedStatement.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT F.id FROM Menu M, Foods F WHERE M.restaurantId=? and F.name=? and F.id=M.foodId ");
            preparedStatement.setString(1, restaurantId);
            preparedStatement.setString(2, foodName);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
                foodId = result.getInt("id");
            result.close();
            preparedStatement.close();
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
            int foodId = getFoodId(foodName, restaurantId);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT F.id FROM PartyMenu M, PartyFoods F " +
                    "WHERE M.restaurantId=? and F.foodId=? and F.id=M.partyfoodId ");
            preparedStatement.setString(1, restaurantId);
            preparedStatement.setInt(2, foodId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next())
                partyfoodId = result.getInt("id");
            result.close();
            preparedStatement.close();
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

    public void addUser(String firstName, String lastName, String email, String password) throws DuplicateEmail {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement pStatement = connection.prepareStatement(
                    "insert into Users (firstName, lastName, credit, email, password) values (?, ?, ?, ?, ?)");
            pStatement.setString(1, firstName);
            pStatement.setString(2, lastName);
            pStatement.setInt(3, 0);
            pStatement.setString(4, email);
            pStatement.setString(5, password);
            pStatement.executeUpdate();
            pStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_DUPLICATE_PK)
                throw new DuplicateEmail();
            else
                e.printStackTrace();
        }
    }

    public UserDTO getUserDTO(String email) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Users WHERE email=?");
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                String firstName = result.getString("firstName");
                String lastName = result.getString("lastName");
                int credit = result.getInt("credit");
                return new UserDTO(firstName, lastName, email, credit);
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
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Foods WHERE  id=?");
                preparedStatement.setInt(1, foodId);
                ResultSet result = preparedStatement.executeQuery();
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
                preparedStatement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                connection = dataSource.getConnection();
                int newFoodId;
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM PartyFoods WHERE id=?");
                preparedStatement.setInt(1, partyFoodId);
                ResultSet result = preparedStatement.executeQuery();
                if (result.next()) {
                    foodDTO.setPrice(result.getInt("newPrice"));
                    newFoodId = result.getInt("foodId");
                }
                else
                    throw new SQLException();
                result.close();
                preparedStatement.close();

                PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM Foods WHERE id=?");
                preparedStatement1.setInt(1, newFoodId);
                ResultSet result2 = preparedStatement1.executeQuery();
                if (result2.next()) {
                    foodDTO.setPopularity(result2.getFloat("popularity"));
                    foodDTO.setImage(result2.getString("imageUrl"));
                    foodDTO.setDescription(result2.getString("description"));
                    foodDTO.setName(result2.getString("name"));
                }
                else
                    throw new SQLException();
                result.close();
                preparedStatement1.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("select * from OrderRows where orderId=?");
            preparedStatement.setInt(1, orderId);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                FoodDTO foodDTO = new FoodDTO();
                foodDTO.setCount(result.getInt("count"));
                FoodDTO food = getFoodDTOById(result.getInt("foodId"), result.getInt("partyFoodId"), result.getString("foodType"));
                foodDTO.setPrice(food.getPrice());
                foodDTO.setName(food.getName());
                foodDTOS.add(foodDTO);
            }
            result.close();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return foodDTOS;
    }

    public ArrayList<OrderDTO> getOrders(String email) {
        ArrayList<OrderDTO> orders = new ArrayList<>();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from Orders where username=?");
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setRestaurantName(getRestaurantById(result.getString("restaurantId")).getName());
                orderDTO.setStatus(result.getString("status"));
                orderDTO.setFoods(getFoodsByOrderId(result.getInt("id")));
                orders.add(orderDTO);
            }
            result.close();
            preparedStatement.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Orders SET status=? WHERE id=?");
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentOrderRestaurantId(String email) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT restaurantId FROM Orders " +
                    "WHERE status=\"notFinalized\" and username=?");
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT count FROM PartyFoods WHERE id=?");
            preparedStatement.setInt(1, foodId);
            ResultSet result = preparedStatement.executeQuery();
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
            PreparedStatement preparedStatement;
            ResultSet result;
            if (foodType.equals("normal")) {
                preparedStatement = connection.prepareStatement("SELECT R.count FROM OrderRows R, Orders O " +
                        "WHERE O.id=? and R.orderId=O.id and R.foodId=?");
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, foodId);
                result = preparedStatement.executeQuery();
            }
            else {
                preparedStatement = connection.prepareStatement("SELECT R.count FROM OrderRows R, Orders O " +
                        "WHERE O.id=? and R.orderId=O.id and R.partyFoodId=?");
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, foodId);
                result = preparedStatement.executeQuery();
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

    public int getCurrentOrderId(String email) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM Orders WHERE username=? and status=\"notFinalized\"");
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
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
            PreparedStatement preparedStatement;
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

                if (foodType.equals("normal")) {
                    preparedStatement = connection.prepareStatement("UPDATE OrderRows SET count=? WHERE orderId=? and foodId=?");
                    preparedStatement.setInt(1, newCount);
                    preparedStatement.setInt(2, orderId);
                    preparedStatement.setInt(3, foodId);
                    preparedStatement.executeUpdate();
                }
                else {
                    preparedStatement = connection.prepareStatement("UPDATE OrderRows SET count=? WHERE orderId=? and partyFoodId=?");
                    preparedStatement.setInt(1, newCount);
                    preparedStatement.setInt(2, orderId);
                    preparedStatement.setInt(3, foodId);
                    preparedStatement.executeUpdate();
                }
            }
            else {
                if (foodType.equals("normal")) {
                    preparedStatement = connection.prepareStatement("INSERT INTO OrderRows (orderId, foodId, count, foodType) values (?, ?, ?, ?)");
                    preparedStatement.setInt(1, orderId);
                    preparedStatement.setInt(2, foodId);
                    preparedStatement.setInt(3, count);
                    preparedStatement.setString(4, "normal");
                    preparedStatement.executeUpdate();
                }
                else {
                    preparedStatement = connection.prepareStatement("INSERT INTO OrderRows (orderId, partyFoodId, count, foodType) values (?, ?, ?, ?)");
                    preparedStatement.setInt(1, orderId);
                    preparedStatement.setInt(2, foodId);
                    preparedStatement.setInt(3, count);
                    preparedStatement.setString(4, "party");
                    preparedStatement.executeUpdate();
                }
            }
            preparedStatement.close();
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
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Orders WHERE id=?");
            preparedStatement.setInt(1, orderId);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement("DELETE FROM OrderRows WHERE orderId=?");
            preparedStatement.setInt(1, orderId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reducePartyFoods(int orderId) {
        Connection connection;
        try {
            connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OrderRows WHERE orderId=? and foodType=\"party\"");
            preparedStatement.setInt(1, orderId);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                preparedStatement = connection.prepareStatement("UPDATE PartyFoods SET count = count-? WHERE id=?");
                preparedStatement.setInt(1, result.getInt("count"));
                preparedStatement.setInt(2, result.getInt("partyFoodId"));
                preparedStatement.executeUpdate();
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT R.* FROM Restaurants R, Orders O WHERE O.id=? and O.restaurantId=R.id");
            preparedStatement.setInt(1, orderId);
            ResultSet result = preparedStatement.executeQuery();
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
