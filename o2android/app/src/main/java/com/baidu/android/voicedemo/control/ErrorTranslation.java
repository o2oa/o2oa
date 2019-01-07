package com.baidu.android.voicedemo.control;

import android.speech.SpeechRecognizer;

/**
 * Created by fujiayi on 2017/6/14.
 */

public class ErrorTranslation {

    public static String recogError(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "音频问题";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "没有语音输入";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "其它客户端错误";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "权限不足";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "网络问题";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "没有匹配的识别结果";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "引擎忙";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "服务端错误";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "连接超时";
                break;
            default:
                message = "未知错误:" + errorCode;
                break;
        }
        return message;
    }

    public static String wakeupError(int errorCode) {
        String message = null;
        switch (errorCode) {
            case 1:
                message = "参数错误";
                break;
            case 2:
                message = "网络请求发生错误";
                break;
            case 3:
                message = "服务器数据解析错误";
                break;
            case 4:
                message = "网络不可用";
                break;
            default:
                message = "未知错误:" + errorCode;
                break;
        }
        return message;
    }
}
