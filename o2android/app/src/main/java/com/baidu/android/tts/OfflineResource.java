package com.baidu.android.tts;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static android.content.ContentValues.TAG;


/**
 * Created by fujiayi on 2017/5/19.
 */

public class OfflineResource {

    public static final String VOICE_FEMALE = "F";

    public static final String VOICE_MALE = "M";


    public static final String VOICE_DUYY = "Y";

    public static final String VOICE_DUXY = "X";

    private static final String SAMPLE_DIR = "baiduTTS";

    private AssetManager assets;
    private String destPath;

    private String textFilename;
    private String modelFilename;

    private static HashMap<String, Boolean> mapInitied = new HashMap<String, Boolean>();

    public OfflineResource(Context context, String voiceType) throws IOException {
        context = context.getApplicationContext();
        this.assets = context.getApplicationContext().getAssets();
        this.destPath = createTmpDir(context);
        setOfflineVoiceType(voiceType);
    }

    private String createTmpDir(Context context) {
        String sampleDir = "baiduASR";
        String samplePath = FileExtensionHelper.getXBPMBaseFolder() + File.separator + sampleDir;
        if (!SDCardHelper.INSTANCE.makeDir(samplePath)) {
            samplePath = context.getExternalFilesDir(sampleDir).getAbsolutePath();
            if (!SDCardHelper.INSTANCE.makeDir(samplePath)) {
                //throw new RuntimeException("创建临时目录失败 :" + samplePath);
            }
        }
        return samplePath;
    }

    public String getModelFilename() {
        return modelFilename;
    }

    public String getTextFilename() {
        return textFilename;
    }

    public void setOfflineVoiceType(String voiceType) throws IOException {
        String text = "bd_etts_text.dat";
        String model;
        if (VOICE_MALE.equals(voiceType)) {
            model = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
        } else if (VOICE_FEMALE.equals(voiceType)) {
            model = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        } else if (VOICE_DUXY.equals(voiceType)) {
            model = "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        } else if (VOICE_DUYY.equals(voiceType)) {
            model = "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
        } else {
            throw new RuntimeException("voice type is not in list");
        }
        textFilename = copyAssetsFile(text);
        modelFilename = copyAssetsFile(model);

    }


    private String copyAssetsFile(String sourceFilename) throws IOException {
        String destFilename = destPath + "/" + sourceFilename;
        boolean recover = false;
        Boolean existed = mapInitied.get(sourceFilename); // 启动时完全覆盖一次
        if (existed == null || !existed) {
            recover = true;
        }
        FileUtil.INSTANCE.copyFromAssets(assets, sourceFilename, destFilename, recover);
        Log.i(TAG, "文件复制成功：" + destFilename);
        return destFilename;
    }


}
