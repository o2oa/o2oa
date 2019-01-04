package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_blue_tooth_server.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import org.jetbrains.anko.doAsync
import java.io.BufferedReader
import java.io.InputStreamReader

class BlueToothServerActivity : BaseMVPActivity<BlueToothContract.View, BlueToothContract.Presenter>(), BlueToothContract.View {

    override var mPresenter: BlueToothContract.Presenter = BlueToothPresenter()


    override fun layoutResId(): Int = R.layout.activity_blue_tooth_server


    // 连接成功
    private val CONN_SUCCESS = 0x1
    // 连接失败
    private val CONN_FAIL = 0x2
    private val RECEIVER_INFO = 0x3

    private var state = CONN_FAIL
    private var isOpen = true

    private val blueToothAdapter: BluetoothAdapter  by lazy { BluetoothAdapter.getDefaultAdapter() }
    private var serverSocket: BluetoothServerSocket? = null

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("服务端")

        messageTv.text = "服务器已经启动，正在等待设备连接...\n"
        doAsync {
            try {
                val btaClass = blueToothAdapter.javaClass
                val method = btaClass.getMethod("listenUsingRfcommOn", Int::class.javaPrimitiveType)
                serverSocket = method.invoke(blueToothAdapter, 29) as BluetoothServerSocket
//                serverSocket = blueToothAdapter.listenUsingInsecureRfcommWithServiceRecord("BlueTest", UUID.fromString(UUID_S))
                while (isOpen) {
                    try {// 阻塞线程等待连接
                        val socket = serverSocket?.accept()
                        // 连接成功发送handler
                        handler.sendEmptyMessage(CONN_SUCCESS)
                        state = CONN_SUCCESS
                        while (state == CONN_SUCCESS) {
                            if (socket?.isConnected == true) {
                                val readIn = BufferedReader(InputStreamReader(socket.inputStream))
                                var text = ""
                                var line = readIn.readLine()
                                while (!TextUtils.isEmpty(line)) {
                                    text += line
                                    line = readIn.readLine()
                                }
                                val mes = handler.obtainMessage(RECEIVER_INFO, text)
                                handler.sendMessage(mes)
                            }
                        }
                    } catch (e: Exception) {
                        XLog.error("", e)
                        val mes = handler.obtainMessage(CONN_FAIL, e.localizedMessage)
                        handler.sendMessage(mes)
                    }

                }

            } catch (e: Exception) {
                XLog.error("", e)
                val mes = handler.obtainMessage(CONN_FAIL, e.localizedMessage)
                handler.sendMessage(mes)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isOpen = false
        try {
            serverSocket?.close()
        } catch (e: Exception) {
        }
    }

    private val handler = Handler(Looper.getMainLooper(), { msg ->
        when (msg?.what) {
            CONN_SUCCESS -> {
                messageTv.text = "连接成功！！！！"
            }
            CONN_FAIL -> {
                messageTv.text = "连接失败！\n" + (msg.obj.toString() + "\n")
            }
            RECEIVER_INFO -> {
                messageTv.text = (msg.obj.toString() + "\n")
            }
        }
        true
    })

}
