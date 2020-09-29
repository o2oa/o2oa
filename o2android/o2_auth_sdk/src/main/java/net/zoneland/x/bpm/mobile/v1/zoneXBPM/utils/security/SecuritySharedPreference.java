package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 自动加密SharedPreference
 * Created by Max on 2016/11/23.
 */

public class SecuritySharedPreference implements SharedPreferences {

    private SharedPreferences mSharedPreferences;
    private static final String TAG = SecuritySharedPreference.class.getName();
    private Context mContext;

    /**
     * constructor
     * @param context should be ApplicationContext not activity
     * @param name file name
     * @param mode context mode
     */
    public SecuritySharedPreference(Context context, String name, int mode){
        mContext = context;
        if (TextUtils.isEmpty(name)){
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            mSharedPreferences =  context.getSharedPreferences(name, mode);
        }

    }

    @Override
    public Map<String, String> getAll() {
        final Map<String, ?> encryptMap = mSharedPreferences.getAll();
        final Map<String, String> decryptMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : encryptMap.entrySet()){
            Object cipherText = entry.getValue();
            if (cipherText != null){
                decryptMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return decryptMap;
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

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        final String encryptValue = mSharedPreferences.getString(encryptPreference(key), null);
        return encryptValue == null ? defValue : decryptPreference(encryptValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        final Set<String> encryptSet = mSharedPreferences.getStringSet(encryptPreference(key), null);
        if (encryptSet == null){
            return defValues;
        }
        final Set<String> decryptSet = new HashSet<>();
        for (String encryptValue : encryptSet){
            decryptSet.add(decryptPreference(encryptValue));
        }
        return decryptSet;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String encryptValue = mSharedPreferences.getString(encryptPreference(key), null);
        if (encryptValue == null) {
            return defValue;
        }
        return Integer.parseInt(decryptPreference(encryptValue));
    }

    @Override
    public long getLong(String key, long defValue) {
        final String encryptValue = mSharedPreferences.getString(encryptPreference(key), null);
        if (encryptValue == null) {
            return defValue;
        }
        return Long.parseLong(decryptPreference(encryptValue));
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String encryptValue = mSharedPreferences.getString(encryptPreference(key), null);
        if (encryptValue == null) {
            return defValue;
        }
        return Float.parseFloat(decryptPreference(encryptValue));
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String encryptValue = mSharedPreferences.getString(encryptPreference(key), null);
        if (encryptValue == null) {
            return defValue;
        }
        return Boolean.parseBoolean(decryptPreference(encryptValue));
    }

    @Override
    public boolean contains(String key) {
        return mSharedPreferences.contains(encryptPreference(key));
    }

    @Override
    public SecurityEditor edit() {
        return new SecurityEditor(mSharedPreferences, mContext);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 处理加密过渡
     */
    public void handleTransition(){
        Map<String, ?> oldMap = mSharedPreferences.getAll();
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : oldMap.entrySet()){
            Log.i(TAG, "key:"+entry.getKey()+", value:"+ entry.getValue());
            newMap.put(encryptPreference(entry.getKey()), encryptPreference(entry.getValue().toString()));
        }
        Editor editor = mSharedPreferences.edit();
        editor.clear().commit();
        for (Map.Entry<String, String> entry : newMap.entrySet()){
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }


}