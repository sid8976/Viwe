package com.sid.viwe;

/**
 * Created by Siddharth on 24-05-2018.
 */

public class Messages {

    public Messages()
    {

    }

    private String message;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
