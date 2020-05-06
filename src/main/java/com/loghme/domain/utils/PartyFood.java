package com.loghme.domain.utils;

public class PartyFood extends Food {
    private int newPrice;
    private int count;

    public void setNewPrice(int newPrice) {this.newPrice = newPrice;}

    public int getNewPrice() {return newPrice;}

    public void setCount(int count) {this.count = count;}

    public int getCount() {return count;}

    public void reduceCount() {
        if (count > 0)
            count -= 1;
    }
}