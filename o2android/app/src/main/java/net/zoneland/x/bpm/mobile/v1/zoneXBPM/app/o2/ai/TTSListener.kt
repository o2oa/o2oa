package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.ai

import android.os.Handler
import com.baidu.android.tts.MessageListener
import com.baidu.tts.client.SpeechError
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancyLou on 15/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */



class TTSListener(val handler: Handler): MessageListener() {


    override fun onSpeechStart(utteranceId: String?) {
        super.onSpeechStart(utteranceId)
        XLog.info("开始说话，$utteranceId")
    }

    override fun onSpeechFinish(utteranceId: String?) {
        super.onSpeechFinish(utteranceId)
        XLog.info("说话结束！$utteranceId")
        send2UIThread(O2AIActivity.HANDLER_ACTION_SPEECH_FINISH, utteranceId ?: "")
    }

    override fun onError(utteranceId: String?, speechError: SpeechError?) {
        super.onError(utteranceId, speechError)
        XLog.error("speech error, utteranceId:$utteranceId"+speechError?.toString())
        send2UIThread(O2AIActivity.HANDLER_ACTION_SPEECH_ERROR, utteranceId ?: "")
    }

    private fun send2UIThread(action: Int, utteranceId:String){
        val msg = handler.obtainMessage()
        msg.what = action
        msg.obj = utteranceId
        handler.sendMessage(msg)
    }
}