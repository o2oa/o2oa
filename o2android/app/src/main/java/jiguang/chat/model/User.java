package jiguang.chat.model;

/**
 * Created by ${chenyn} on 2017/8/14.
 */

public class User {
    public String appkey;
    public String username;
    public String platform;

    public User(String appkey, String username, String platform) {
        this.appkey = appkey;
        this.username = username;
        this.platform = platform;
    }
}
