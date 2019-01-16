package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.ai

import android.os.Handler
import com.baidu.android.voicedemo.recognization.RecogResult
import com.baidu.android.voicedemo.recognization.StatusRecogListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancyLou on 15/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */



class AsrRecognizeStatusListener(val handler: Handler): StatusRecogListener() {



    override fun onAsrPartialResult(results: Array<out String>?, recogResult: RecogResult?) {
        XLog.info("临时识别结果，结果是“" + results?.get(0) + "”；原始json：" + recogResult?.origalJson)
        super.onAsrPartialResult(results, recogResult)
    }

    override fun onAsrFinalResult(results: Array<out String>?, recogResult: RecogResult?) {
        val result = results?.get(0)
        XLog.info("识别结束，结果是”" + result + "”“；原始json：" + recogResult?.origalJson)
        super.onAsrFinalResult(results, recogResult)
        // 开始处理识别结果
        send2UIThread(O2AIActivity.HANDLER_ACTION_LISTEN_FINISH, result?:"")
    }

    override fun onAsrReady() {
        super.onAsrReady()
        XLog.debug("引擎就绪，可以开始说话。")
//        presenter.onAsrReady()
    }

    override fun onAsrBegin() {
        super.onAsrBegin()
        XLog.debug("检测到用户说话")
    }

    override fun onAsrEnd() {
        super.onAsrEnd()
        XLog.debug("检测到用户说话结束")
    }

    override fun onAsrFinish(recogResult: RecogResult?) {
        super.onAsrFinish(recogResult)
        XLog.debug("识别一段话结束。如果是长语音的情况会继续识别下段话。")
    }

    override fun onAsrFinishError(errorCode: Int, subErrorCode: Int, errorMessage: String?, descMessage: String?, recogResult: RecogResult?) {
        val message = "识别错误, 错误码：$errorCode ,$subErrorCode ; $descMessage"
        super.onAsrFinishError(errorCode, subErrorCode, errorMessage, descMessage, recogResult)
        send2UIThread(O2AIActivity.HANDLER_ACTION_LISTEN_ERROR, "$message；错误消息:$errorMessage；描述信息：$descMessage")
    }

    override fun onAsrLongFinish() {
        super.onAsrLongFinish()
        XLog.debug("长语音识别结束。")
    }

    override fun onAsrExit() {
        super.onAsrExit()
        XLog.debug("识别引擎结束并空闲中")
    }

    override fun onAsrOnlineNluResult(nluResult: String?) {
        super.onAsrOnlineNluResult(nluResult)
        if (nluResult?.isEmpty() == false) {
            XLog.info("原始语义识别结果json：$nluResult")
        }
    }

    override fun onOfflineLoaded() {
        super.onOfflineLoaded()
        XLog.debug("【重要】asr.loaded：离线资源加载成功。没有此回调可能离线语法功能不能使用。")
    }

    override fun onOfflineUnLoaded() {
        super.onOfflineUnLoaded()
        XLog.debug(" 离线资源卸载成功。")
    }

    private fun send2UIThread(action: Int, result:String) {
        val msg = handler.obtainMessage()
        msg.what = action
        msg.obj = result
        handler.sendMessage(msg)
    }
}