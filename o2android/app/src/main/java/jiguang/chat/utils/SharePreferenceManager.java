package jiguang.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceManager {
    static SharedPreferences sp;

    public static void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    private static final String KEY_CACHED_USERNAME = "jchat_cached_username";

    public static void setCachedUsername(String username) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_USERNAME, username).apply();
        }
    }

    public static String getCachedUsername() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_USERNAME, null);
        }
        return null;
    }

    private static final String KEY_CACHED_PSW = "jchat_cached_psw";

    public static void setCachedPsw(String psw) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_PSW, psw).apply();
        }
    }

    public static String getCachedPsw() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_PSW, null);
        }
        return null;
    }

    private static final String KEY_REGISTER_USERNAME = "key_register_username";

    public static void setRegisterUsername(String username) {
        if (null != sp) {
            sp.edit().putString(KEY_REGISTER_USERNAME, username).apply();
        }
    }

    public static String getRegistrUsername() {
        if (null != sp) {
            return sp.getString(KEY_REGISTER_USERNAME, null);
        }
        return null;
    }

    private static final String REGISTER_NAME = "register_name";

    public static void setRegisterName(String username) {
        if (null != sp) {
            sp.edit().putString(REGISTER_NAME, username).apply();
        }
    }

    public static String getRegistrName() {
        if (null != sp) {
            return sp.getString(REGISTER_NAME, null);
        }
        return null;
    }

    private static final String REGISTER_PASS = "register_pass";

    public static void setRegistePass(String pass) {
        if (null != sp) {
            sp.edit().putString(REGISTER_PASS, pass).apply();
        }
    }

    public static String getRegistrPass() {
        if (null != sp) {
            return sp.getString(REGISTER_PASS, null);
        }
        return null;
    }

    private static final String ADD_FRIEND_ITEM = "item";

    public static void setItem(Long pass) {
        if (null != sp) {
            sp.edit().putLong(ADD_FRIEND_ITEM, pass).apply();
        }
    }

    public static Long getItem() {
        if (null != sp) {
            return sp.getLong(ADD_FRIEND_ITEM, 0);
        }
        return null;
    }

    private static final String KEY_CACHED_AVATAR_PATH = "jchat_cached_avatar_path";

    public static void setCachedAvatarPath(String path) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_AVATAR_PATH, path).apply();
        }
    }

    public static String getCachedAvatarPath() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_AVATAR_PATH, null);
        }
        return null;
    }

    private static final String CONVERSATION_TOP = "conversation_top";

    public static void setTopSize(int num) {
        if (null != sp) {
            sp.edit().putInt(CONVERSATION_TOP, num).apply();
        }
    }

    public static int getTopSize() {
        if (null != sp) {
            return sp.getInt(CONVERSATION_TOP, 0);
        }
        return 0;
    }

    private static final String CONVERSATION_TOP_CANCEL = "conversation_top_cancel";

    public static void setCancelTopSize(int num) {
        if (null != sp) {
            sp.edit().putInt(CONVERSATION_TOP_CANCEL, num).apply();
        }
    }

    public static int getCancelTopSize() {
        if (null != sp) {
            return sp.getInt(CONVERSATION_TOP_CANCEL, 0);
        }
        return 0;
    }


    private static final String KEY_REGISTER_AVATAR_PATH = "jchat_register_avatar_path";

    public static void setRegisterAvatarPath(String path) {
        if (null != sp) {
            sp.edit().putString(KEY_REGISTER_AVATAR_PATH, path).apply();
        }
    }

    public static String getRegisterAvatarPath() {
        if (null != sp) {
            return sp.getString(KEY_REGISTER_AVATAR_PATH, null);
        }
        return null;
    }

    private static final String KEY_CACHED_FIX_PROFILE_FLAG = "fixProfileFlag";

    public static void setCachedFixProfileFlag(boolean value) {
        if(null != sp){
            sp.edit().putBoolean(KEY_CACHED_FIX_PROFILE_FLAG, value).apply();
        }
    }

    public static boolean getCachedFixProfileFlag(){
        return null != sp && sp.getBoolean(KEY_CACHED_FIX_PROFILE_FLAG, false);
    }

    private static final String NO_DISTURB = "no_disturb";

    public static void setNoDisturb(boolean value) {
        if(null != sp){
            sp.edit().putBoolean(NO_DISTURB, value).apply();
        }
    }

    public static boolean getNoDisturb(){
        return null != sp && sp.getBoolean(NO_DISTURB, false);
    }

    private static final String IS_SHOWCHECK = "isShowCheck";

    public static void setShowCheck(boolean value) {
        if(null != sp){
            sp.edit().putBoolean(NO_DISTURB, value).apply();
        }
    }

    public static boolean getShowCheck(){
        return null != sp && sp.getBoolean(NO_DISTURB, false);
    }

    private static final String ISOPEN = "isopen";

    public static void setIsOpen(boolean value) {
        if(null != sp){
            sp.edit().putBoolean(ISOPEN, value).apply();
        }
    }

    public static boolean getIsOpen(){
        return null != sp && sp.getBoolean(ISOPEN, false);
    }

    private static final String SOFT_KEYBOARD_HEIGHT = "SoftKeyboardHeight";

    public static void setCachedKeyboardHeight(int height){
        if(null != sp){
            sp.edit().putInt(SOFT_KEYBOARD_HEIGHT, height).apply();
        }
    }

    public static int getCachedKeyboardHeight(){
        if(null != sp){
            return sp.getInt(SOFT_KEYBOARD_HEIGHT, 0);
        }
        return 0;
    }

    private static final String WRITABLE_FLAG = "writable";
    public static void setCachedWritableFlag(boolean value){
        if(null != sp){
            sp.edit().putBoolean(WRITABLE_FLAG, value).apply();
        }
    }

    public static boolean getCachedWritableFlag(){
        return null == sp || sp.getBoolean(WRITABLE_FLAG, true);
    }

    private static final String CACHED_APP_KEY = "CachedAppKey";

    public static void setCachedAppKey(String appKey) {
        if (null != sp) {
            sp.edit().putString(CACHED_APP_KEY, appKey).apply();
        }
    }

    public static String getCachedAppKey() {
        if (null != sp) {
            return sp.getString(CACHED_APP_KEY, "default");
        }
        return "default";
    }

    private static final String CACHED_NEW_FRIEND = "CachedNewFriend";

    public static void setCachedNewFriendNum(int num) {
        if (null != sp) {
            sp.edit().putInt(CACHED_NEW_FRIEND, num).apply();
        }
    }

    public static int getCachedNewFriendNum() {
        if (null != sp) {
            return sp.getInt(CACHED_NEW_FRIEND, 0);
        }
        return 0;
    }
}
