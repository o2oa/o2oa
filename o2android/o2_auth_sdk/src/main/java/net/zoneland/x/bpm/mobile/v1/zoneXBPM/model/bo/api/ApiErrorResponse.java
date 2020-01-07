package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api;

/**
 * Created by fancy on 2017/3/23.
 */

public class ApiErrorResponse {

    private String type;//success
    private String date;
    private int spent;
    private int size;
    private int count;
    private int position;
    private String message;
    private String prompt;


    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSpent() {
        return spent;
    }

    public void setSpent(int spent) {
        this.spent = spent;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
