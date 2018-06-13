package com.sid.viwe;

/**
 * Created by Siddharth on 23-05-2018.
 */

public class Request
{
    public Request()
    {

    }
    public Request(String name, String thumb_image, String image) {
        this.name = name;
        this.thumb_image = thumb_image;
        this.image = image;
    }
    public String name;
    public String thumb_image;

    public String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
