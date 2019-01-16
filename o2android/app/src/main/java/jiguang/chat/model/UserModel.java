package jiguang.chat.model;

/**
 * Created by ${chenyn} on 2017/8/14.
 */

public class UserModel<T> {
    public String type;
    public T user;

    public UserModel(String type, T user) {
        this.type = type;
        this.user = user;
    }
}
