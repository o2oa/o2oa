package com.baidu.android.voicedemo.recognization.offline;

import android.app.Activity;
import android.content.SharedPreferences;

import com.baidu.android.voicedemo.recognization.CommonRecogParams;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fujiayi on 2017/6/13.
 */

public class OfflineRecogParams extends CommonRecogParams {

    private static final String TAG = "OnlineRecogParams";

    public OfflineRecogParams(Activity context) {
        super(context);
    }


    public Map<String, Object> fetch(SharedPreferences sp) {

        Map<String, Object> map = super.fetch(sp);
        map.put(SpeechConstant.DECODER, 2);
        map.remove(SpeechConstant.PID); // 去除pid，只支持中文
        return map;

    }

    public static Map<String, Object> fetchOfflineParams() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(SpeechConstant.DECODER, 2);
        map.put(SpeechConstant.ASR_OFFLINE_ENGINE_GRAMMER_FILE_PATH, "assets:///baidu_speech_grammar.bsg");
        map.putAll(fetchSlotDataParam());
        return map;
    }

    public static Map<String, Object> fetchSlotDataParam() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            JSONObject json = new JSONObject();
            json.put("name", new JSONArray().put("妈妈").put("老伍"))
                    .put("appname", new JSONArray().put("手百").put("度秘"));
            map.put(SpeechConstant.SLOT_DATA, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

}
