package com.loghme.service.DTO;

public class FoodDTO {
    private String name;
    private int price;
    private int count;
    private String description;
    private float popularity;
    private String image;

    public FoodDTO() {}

    public FoodDTO(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public FoodDTO(String name, int price, String description, float popularity, String image) {
        this.name = name;
        this.price = price;
        this.popularity = popularity;
        this.description = description;
        this.image = image;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public int getCount() { return count; }

    public void setCount(int count) { this.count = count; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public float getPopularity() { return popularity; }

    public void setPopularity(float popularity) { this.popularity = popularity; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }
}
