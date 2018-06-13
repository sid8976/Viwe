package com.sid.viwe;

/**
 * Created by Siddharth on 25-04-2018.
 */

public class userss {

    public userss(String image, String name, String thumb_image, String status) {
        this.image = image;
        this.name = name;
        this.thumb_image = thumb_image;
        this.status = status;
    }

    public userss()
    {

    }

    public String image;
    public String name;
    public String thumb_image;
    public String status;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
