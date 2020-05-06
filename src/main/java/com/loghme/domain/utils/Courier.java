package com.loghme.domain.utils;

public class Courier {
    private String id;
    private int velocity;
    private Location location;

    public void setId(String id) {this.id = id;}

    public String getId() {return id;}

    public void setVelocity(int velocity) {this.velocity = velocity;}

    public int getVelocity() {return velocity;}

    public void setLocation(Location location) {this.location = location;}

    public Location getLocation() {return location;}
}
