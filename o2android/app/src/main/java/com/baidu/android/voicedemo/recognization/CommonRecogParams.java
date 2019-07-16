package com.baidu.android.voicedemo.recognization;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.speech.asr.SpeechConstant;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fujiayi on 2017/6/20.
 */

public class CommonRecogParams {

    protected String samplePath;
    /**
     * 字符串格式的参数
     */
    protected ArrayList<String> stringParams = new ArrayList<String>();

    /**
     * int格式的参数
     */
    protected ArrayList<String> intParams = new ArrayList<String>();

    /**
     * bool格式的参数
     */
    protected ArrayList<String> boolParams = new ArrayList<String>();

    private static final String TAG = "CommonRecogParams";

    public CommonRecogParams(Activity context) {

        stringParams.addAll(Arrays.asList(
                SpeechConstant.VAD,
                SpeechConstant.IN_FILE
        ));
        intParams.addAll(Arrays.asList(
                SpeechConstant.PID,
                SpeechConstant.VAD_ENDPOINT_TIMEOUT
        ));
        boolParams.addAll(Arrays.asList(
                SpeechConstant.ACCEPT_AUDIO_DATA,
                SpeechConstant.ACCEPT_AUDIO_VOLUME
        ));
        initSamplePath(context);
    }

    /**
     * 创建保存OUTFILE的临时目录. 仅用于OUTFILE参数。不使用demo中的OUTFILE参数可忽略此段
     *
     * @param context
     */
    protected void initSamplePath(Activity context) {
        String sampleDir = "baiduASR";
        samplePath = FileExtensionHelper.getXBPMBaseFolder() + File.separator + sampleDir;
        if (!SDCardHelper.INSTANCE.makeDir(samplePath)) {
            samplePath = context.getApplication().getExternalFilesDir(sampleDir).getAbsolutePath();
            if (!SDCardHelper.INSTANCE.makeDir(samplePath)) {
                throw new RuntimeException("创建临时目录失败 :" + samplePath);
            }
        }
    }

    public Map<String, Object> fetch(SharedPreferences sp) {
        Map<String, Object> map = new HashMap<String, Object>();

        parseParamArr(sp, map);

        if (sp.getBoolean("_tips_sound", false)) { // 声音回调
            map.put(SpeechConstant.SOUND_START, R.raw.bdspeech_recognition_start);
            map.put(SpeechConstant.SOUND_END, R.raw.bdspeech_speech_end);
            map.put(SpeechConstant.SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            map.put(SpeechConstant.SOUND_ERROR, R.raw.bdspeech_recognition_error);
            map.put(SpeechConstant.SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }

        if (sp.getBoolean("_outfile", false)) { // 保存录音文件
            map.put(SpeechConstant.ACCEPT_AUDIO_DATA, true); // 目前必须开启此回掉才嫩保存音频
            map.put(SpeechConstant.OUT_FILE, samplePath + "/outfile.pcm");
            Log.i(TAG, "语音录音文件将保存在：" + samplePath + "/outfile.pcm");
        }

        return map;
    }

    /**
     * 根据 stringParams intParams boolParams中定义的参数名称，提取SharedPreferences相关字段
     *
     * @param sp
     * @param map
     */
    private void parseParamArr(SharedPreferences sp, Map<String, Object> map) {
        for (String name : stringParams) {
            if (sp.contains(name)) {
                String tmp = sp.getString(name, "").replaceAll(",.*", "").trim();
                if (null != tmp && !"".equals(tmp)) {
                    map.put(name, tmp);
                }
            }
        }
        for (String name : intParams) {
            if (sp.contains(name)) {
                String tmp = sp.getString(name, "").replaceAll(",.*", "").trim();
                if (null != tmp && !"".equals(tmp)) {
                    map.put(name, Integer.parseInt(tmp));
                }
            }
        }
        for (String name : boolParams) {
            if (sp.contains(name)) {
                boolean res =  sp.getBoolean(name, false);
                if (res || name.equals(SpeechConstant.ACCEPT_AUDIO_VOLUME)){
                    map.put(name, res);
                }
            }
        }
    }
}

