package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.security;

/**
 * Created by fancyLou on 2020-09-29.
 * Copyright © 2020 O2. All rights reserved.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

/**
 * 自动加密Editor
 */
public class SecurityEditor implements SharedPreferences.Editor {

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private SharedPreferences.Editor mEditor;

    /**
     * constructor
     */
    public SecurityEditor(SharedPreferences sharedPreferences, Context context){
        this.mContext = context;
        this.mSharedPreferences = sharedPreferences;
        this.mEditor = mSharedPreferences.edit();
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        mEditor.putString(encryptPreference(key), encryptPreference(value));
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        final Set<String> encryptSet = new HashSet<>();
        for (String value : values){
            encryptSet.add(encryptPreference(value));
        }
        mEditor.putStringSet(encryptPreference(key), encryptSet);
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        mEditor.putString(encryptPreference(key), encryptPreference(Integer.toString(value)));
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        mEditor.putString(encryptPreference(key), encryptPreference(Long.toString(value)));
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        mEditor.putString(encryptPreference(key), encryptPreference(Float.toString(value)));
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        mEditor.putString(encryptPreference(key), encryptPreference(Boolean.toString(value)));
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        mEditor.remove(encryptPreference(key));
        return this;
    }

    /**
     * encrypt function
     * @return cipherText base64
     */
    private String encryptPreference(String plainText){
        return EncryptUtil.getInstance(mContext).encrypt(plainText);
    }

    /**
     * decrypt function
     * @return plainText
     */
    private String decryptPreference(String cipherText){
        return EncryptUtil.getInstance(mContext).decrypt(cipherText);
    }

    /**
     * Mark in the editor to remove all values from the preferences.
     * @return this
     */
    @Override
    public SharedPreferences.Editor clear() {
        mEditor.clear();
        return this;
    }

    /**
     * 提交数据到本地
     * @return Boolean 判断是否提交成功
     */
    @Override
    public boolean commit() {

        return mEditor.commit();
    }

    /**
     * Unlike commit(), which writes its preferences out to persistent storage synchronously,
     * apply() commits its changes to the in-memory SharedPreferences immediately but starts
     * an asynchronous commit to disk and you won't be notified of any failures.
     */
    @Override
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void apply() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mEditor.apply();
        } else {
            commit();
        }
    }
}
