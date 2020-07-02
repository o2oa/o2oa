package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.WsMsgQueue
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import android.os.HandlerThread
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.O2IM
import org.json.JSONTokener
import org.json.JSONObject
import java.lang.Exception


class WebSocketService : Service() {


    private var mWebSocket: WebSocket? = null
    private val queue: WsMsgQueue<String> = WsMsgQueue()

    private val heartbeatMsgWhat = 1024
    private lateinit var heartbeatHandler: Handler

    override fun onCreate() {
        super.onCreate()
        XLog.debug("WebSocketService onCreate..............")
        val handlerThread = HandlerThread("serviceCalculate")
        handlerThread.start()
        heartbeatHandler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message?) {
                if (msg != null &&  msg.what == heartbeatMsgWhat) {
                    XLog.debug("发送 ws 心跳消息。。。。。")
                    sendWsMessage("heartbeat")//发送心跳
                }
                //30秒发送一次心跳
                sendMessageDelayed(obtainMessage(heartbeatMsgWhat), 30000)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        XLog.debug("WebSocketService onBind...........")
        return WebSocketBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        XLog.debug("WebSocketService onUnbind...........")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        XLog.debug("WebSocketService onDestroy...........")
        webSocketClose()
        super.onDestroy()
    }


    /**
     * 开启 webSocket连接 登录成功的时候、MainActivity绑定的时候
     */
    fun webSocketOpen() {
        XLog.debug("WebSocketService webSocketOpen...........")
        RetrofitClient.instance().openWebSocket(O2WebSocketListener())
    }

    /**
     * 关闭 webSocket连接 退出登录的时候、Service销毁的时候
     */
    fun webSocketClose() {
        XLog.debug("WebSocketService webSocketClose...........")
        heartbeatHandler.removeMessages(heartbeatMsgWhat)
        if (mWebSocket != null) {
            mWebSocket?.close(1000, "close")
        }
        mWebSocket = null
//        RetrofitClient.instance().closeWebSocket()
    }

    /**
     * 是否连接
     */
    fun isWebSocketOpen(): Boolean {
        return (mWebSocket != null)
    }

    
    /**
     * 发送消息
     */
    fun sendWsMessage(message: String) {
        queue.add(message)
        if (mWebSocket == null) {
            webSocketOpen()
        }else {
            realSendMessage()
        }
    }

    /**
     * im消息广播发送
     */
    fun sendIMMessageBroadcast(msg: String) {
        XLog.debug("发送im 消息： $msg")
        val intent = Intent()
        intent.action = O2IM.IM_Message_Receiver_Action
        intent.putExtra(O2IM.IM_Message_Receiver_name, msg)
        sendBroadcast(intent)
    }

    /**
     * 发送消息
     */
    private fun realSendMessage() {
        if (queue.size()>0) {
            for (i in 1..queue.size()) {
                val message = queue.get()
                if (message != null) {
                    mWebSocket?.send(message)
                }
            }
        }
    }

    /**
     * 心跳消息
     */
    private fun heartbeatMessage() {
        heartbeatHandler.sendMessage(heartbeatHandler.obtainMessage(heartbeatMsgWhat))
    }


    inner class O2WebSocketListener: WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            XLog.info("webSocket 连接成功 ！！！")
            mWebSocket = webSocket
            heartbeatMessage()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            //忽略心跳消息
            if ("heartbeat" != text) {
                XLog.info("webSocket 收到消息， message:$text")
                val json = JSONTokener(text).nextValue()
                if (json is JSONObject) {
                    try {
                        val type = json.getString("type")
                        //发送im消息
                        if (type == "im_create") {
                            val body = json.getJSONObject("body")
                            sendIMMessageBroadcast(body.toString())
                        }
                    } catch (e: Exception) {
                        XLog.error("", e)
                    }
                }
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            XLog.info("webSocket 连接已关闭 ！！！")
            super.onClosed(webSocket, code, reason)
            mWebSocket = null
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            XLog.error("webSocket 连接错误 ", t)
            mWebSocket = null
        }
    }


    inner class WebSocketBinder : Binder() {

        val service: WebSocketService
            get() = this@WebSocketService

    }

}