package jiguang.chat.model;

import android.graphics.Bitmap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by ${chenyn} on 2017/3/14.
 */

public class InfoModel {
    public  UserInfo friendInfo;
    public  Bitmap bitmap;

    private static InfoModel mInfoModel = new InfoModel();

    public static InfoModel getInstance() {
        return mInfoModel;
    }

    public Long getUid () {
        return friendInfo.getUserID();
    }

    public void setBitmap(Bitmap avatar) {
        bitmap = avatar;
    }

    public Bitmap getAvatar() {
        return bitmap;
    }

    public String getNoteName() {
        return friendInfo.getNotename();
    }

    public boolean isFriend() {
        return friendInfo.isFriend();
    }

    public String getNickName() {
        return friendInfo.getNickname();
    }


    public String getUserName() {
        return friendInfo.getUserName();
    }

    public String getSign() {
        return friendInfo.getSignature();
    }

    public String getAvatarPath() {
        File avatarFile = friendInfo.getAvatarFile();
        if (avatarFile != null) {
            return avatarFile.getPath();
        }
        return null;
    }

    public String getGender() {
        UserInfo.Gender gender = friendInfo.getGender();
        if (gender != null) {
            if (gender.equals(UserInfo.Gender.male)) {
                return "男";
            } else if (gender.equals(UserInfo.Gender.female)) {
                return "女";
            } else {
                return "保密";
            }
        } else {
            return "保密";
        }
    }

    public String getAppKey() {
        return friendInfo.getAppKey();
    }

    public String getBirthday() {
        long birthday = friendInfo.getBirthday();
        Date date = new Date(birthday);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormat.format(date);
    }

    public String getCity() {
        return friendInfo.getRegion();
    }

}
