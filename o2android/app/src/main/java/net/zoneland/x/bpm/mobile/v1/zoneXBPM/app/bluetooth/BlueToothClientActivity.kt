package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_blue_tooth_client.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.DividerItemDecoration
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.collections.ArrayList


class BlueToothClientActivity : BaseMVPActivity<BlueToothContract.View, BlueToothContract.Presenter>(), BlueToothContract.View {

    // 连接成功
    private val CONN_SUCCESS = 0x1
    // 连接失败
    private val CONN_FAIL = 0x2

    val REQUEST_ENABLE_BT = 1001


    override var mPresenter: BlueToothContract.Presenter = BlueToothPresenter()
    override fun layoutResId(): Int = R.layout.activity_blue_tooth_client
    private val blueToothAdapter: BluetoothAdapter  by lazy {
        val blueManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        blueManager.adapter
    }
    private var socket: BluetoothSocket? = null
    private var isConnect = false

    private val wifiSSIDs = ArrayList<String>()
    private val wifiListAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, wifiSSIDs, R.layout.item_text1) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                holder?.setText(android.R.id.text1, t ?: "unknown")
            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("连接AI设备", true)

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(discoveryDeviceBroadcast, intentFilter)

        scanLayout.setOnClickListener {
            scanDevice()
        }

        wifiListAdapter.setOnItemClickListener { _, position ->
            XLog.info("position:$position")
            val ssid = wifiSSIDs[position]
            XLog.info("ssid:$ssid")

            val dialog = O2DialogSupport.openCustomViewDialog(this@BlueToothClientActivity, "请输入wifi密码", R.layout.dialog_wifi, { dialog ->
                val wifiPasswordTv = dialog.findViewById<EditText>(R.id.dialog_wifi_password)
                val password = wifiPasswordTv.text.toString()
                if (TextUtils.isEmpty(password)) {
                    XToast.toastShort(this@BlueToothClientActivity, "请输入WIFI密码")
                } else {
                    sendInfo2AIDevice(ssid, password)
                }
            })
            val wifiSSIDTv = dialog.findViewById<TextView>(R.id.dialog_wifi_ssid)
            wifiSSIDTv.text = ssid

        }
        listWifi.adapter = wifiListAdapter
        listWifi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listWifi.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        if (!blueToothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            scanDevice()
        }

        val wifiManger = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val listResult = wifiManger.scanResults
        listResult?.map {
            XLog.info("wifi:" + it.SSID)
            if (!TextUtils.isEmpty(it.SSID)) {
                wifiSSIDs.add(it.SSID!!)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_ENABLE_BT -> {
                    scanDevice()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(discoveryDeviceBroadcast)
        try {
            socket?.close()
        } catch (e: Exception) {
        }
    }

    private fun scanDevice() {
        radarInScan.isSearching = true
        scanTitle.text = "正在扫描AI设备..."
        receiveMessageTv.text = ""
        if (blueToothAdapter.isDiscovering) {
            blueToothAdapter.cancelDiscovery()
        }
        blueToothAdapter.startDiscovery()
    }


    private fun sendInfo2AIDevice(wifiSSID: String, wifiPassword: String) {
        try {
            //todo httpProtocol
            val centerHost = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_HOST_KEY, "")
            val centerContext = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_CONTEXT_KEY, "")
            val centerPort = O2SDKManager.instance().prefs().getInt(O2.PRE_CENTER_PORT_KEY, 0)
            val info = AIDeviceBluetoothSendInfo(wifiSSID, wifiPassword, centerHost, centerContext, centerPort.toString(), O2SDKManager.instance().zToken)
            val json = O2SDKManager.instance().gson.toJson(info)
            XLog.info("json:" + json)
            val out = socket?.outputStream
            out?.write(json.toByteArray())
            out?.flush()
            XToast.toastShort(this, "AI设备写入信息成功！")
            finish()
        } catch (e: Exception) {
            XLog.error("写入错误。。。。。。。。。。", e)
            XToast.toastShort(this, "AI设备写入错误！" + e.message)
        }
    }


    private val handler = object : Handler() {

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                CONN_SUCCESS -> {
                    runOnUiThread {
                        radarInScan.isSearching = false
                        radarInScan.gone()
                        receiveMessageTv.text = "找到AI设备，请选择下列可连接的wifi网络"
                        listWifi.visible()
                        wifiListAdapter.notifyDataSetChanged()
                    }
                }
                CONN_FAIL -> {
                    radarInScan.isSearching = false
                    scanTitle.text = "扫描"
                    receiveMessageTv.text = "连接失败！\n 请点击扫描按钮重新扫描连接"

                }
            }
        }
    }


    private val discoveryDeviceBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    XLog.info("开始扫描蓝牙设备。。。。。。")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    XLog.info("结束扫描蓝牙。。。。。。。")
                    if (!isConnect) {
                        val mes = handler.obtainMessage(CONN_FAIL, "未找到AI设备")
                        handler.sendMessage(mes)
                    }
                }
                BluetoothDevice.ACTION_FOUND -> {
                    XLog.info("找到一个蓝牙设备")
                    //  EXTRA_DEVICE  , EXTRA_CLASS
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    XLog.info("name: ${device.name}, address:${device.address}, state:${device.bondState}")
                    if ("B8:27:EB:B6:1E:24" == device.address) {
                        connect2Device(device)
                    }
                }
            }
        }
    }

    private fun connect2Device(device: BluetoothDevice?) {
        XLog.info("connect device.............")
        doAsync {
            try {
                socket = device?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
//            val method = device?.javaClass?.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
//            socket = method?.invoke(device, 1) as BluetoothSocket
                if (socket != null) {
                    // 连接
                    socket?.connect()
                }
                isConnect = true
                handler.sendEmptyMessage(CONN_SUCCESS)
            } catch (e: Exception) {
                XLog.error("", e)
                val mes = handler.obtainMessage(CONN_FAIL, e.localizedMessage)
                handler.sendMessage(mes)
            }
        }
    }


    private data class AIDeviceBluetoothSendInfo(var wifi_ssid: String = "",
                                                 var wifi_pwd: String = "",
                                                 var center_host: String = "",
                                                 var center_context: String = "",
                                                 var center_port: String = "",
                                                 var xtoken: String = "")
}
