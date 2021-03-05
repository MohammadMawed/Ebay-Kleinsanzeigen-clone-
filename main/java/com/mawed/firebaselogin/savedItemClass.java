package com.mawed.firebaselogin;

public class savedItemClass {
    private String description;
    private String Time;
    private String price;
    private String city;
    private String imageID;
    private String category;
    private String ImageUri;
    private String userID;

    public savedItemClass() {
    }


    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String ImageUri) {
        this.ImageUri = ImageUri;
    }
}
