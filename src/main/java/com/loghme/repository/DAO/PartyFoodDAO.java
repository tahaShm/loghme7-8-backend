package com.loghme.repository.DAO;

public class PartyFoodDAO extends FoodDAO {
    private int newPrice;
    private String valid;

    public int getNewPrice() { return newPrice; }

    public void setNewPrice(int newPrice) { this.newPrice = newPrice; }

    public String getValid() { return valid; }

    public void setValid(String valid) { this.valid = valid; }
}
