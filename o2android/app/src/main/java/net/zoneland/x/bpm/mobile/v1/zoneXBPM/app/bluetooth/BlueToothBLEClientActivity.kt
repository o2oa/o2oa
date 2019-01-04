package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bluetooth

import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_blue_tooth_bleclient.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import org.jetbrains.anko.doAsync


class BlueToothBLEClientActivity : BaseMVPActivity<BlueToothContract.View, BlueToothContract.Presenter>(), BlueToothContract.View {

    override var mPresenter: BlueToothContract.Presenter = BlueToothPresenter()

    override fun layoutResId(): Int = R.layout.activity_blue_tooth_bleclient


    val REQUEST_ENABLE_BT = 1001
    val UUID_C = "00000000-2527-eef3-ffff-ffffe3160865"
    val address = "F0:43:47:89:53:BC"
//    val address = "54:BB:4B:FF:9D:A6"


    private var mBlueAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mBluetoothCharacteristic: BluetoothGattCharacteristic? = null

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("客户端")

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) { //是否支持蓝牙BLE
            XToast.toastShort(this, "设备不支持蓝牙BLE功能！")
            finish()
        }
        val blueManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBlueAdapter = blueManager.adapter
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(discoveryDeviceBroadcast, intentFilter)

        sendMessageBtn.setOnClickListener {
            val text = sendTextEdit.text.toString()
            if (TextUtils.isEmpty(text)) {
                XToast.toastShort(this, "请输入发送内容")
                return@setOnClickListener
            }
            //写入蓝牙服务端
            mBluetoothGatt?.setCharacteristicNotification(mBluetoothCharacteristic, true)
            mBluetoothCharacteristic?.setValue(text)
            mBluetoothGatt?.writeCharacteristic(mBluetoothCharacteristic)
        }

    }

    override fun onResume() {
        super.onResume()
        startBluetoothClient()
    }

    override fun onPause() {
        super.onPause()
        pauseBluetoothClient()
    }

    override fun onStop() {
        super.onStop()
        mBluetoothGatt?.close()
        mBluetoothGatt = null
    }

    private fun pauseBluetoothClient() {
        if (mBlueAdapter?.isEnabled == true) {
            if (mBlueAdapter?.isDiscovering ==true) {
                mBlueAdapter?.cancelDiscovery()
            }

            mBluetoothGatt?.disconnect()
        }
    }


    private fun startBluetoothClient() {
        if (mBlueAdapter != null) {
            if (mBlueAdapter?.isEnabled != true) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                scanDevice()
            }
        } else {
            messageTv.text = "没有获取到蓝牙适配器。。。。"
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


    private fun scanDevice() {
        if (mBlueAdapter?.isDiscovering == true) {
            mBlueAdapter?.cancelDiscovery()
        }
        mBlueAdapter?.startDiscovery()

    }


    private val discoveryDeviceBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when (action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    XLog.info("开始扫描蓝牙设备。。。。。。")
                    runOnUiThread {
                        messageTv.text = "开始扫描蓝牙设备。。。。。。"
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    XLog.info("结束扫描蓝牙。。。。。。。")
                }
                BluetoothDevice.ACTION_FOUND -> {
                    XLog.info("找到一个蓝牙设备")
                    //  EXTRA_DEVICE  , EXTRA_CLASS
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    XLog.info("name: ${device.name}, address:${device.address}, state:${device.bondState}")

                    if (address == device.address && device.bondState == BluetoothDevice.BOND_NONE) {
                        connect2Device(device)
                    }

                }
            }
        }
    }

    private fun connect2Device(device: BluetoothDevice?) {
        messageTv.text = "正在连接蓝牙设备。。。。。。"
        mBluetoothGatt = device?.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                when (newState) {
                    BluetoothGatt.STATE_CONNECTED -> {
                        runOnUiThread {
                            messageTv.text = "连接蓝牙设备连接成功。。。。。。"
                        }
                        gatt?.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        runOnUiThread {
                            messageTv.text = "连接蓝牙设备已经断开连接。。。。。。"
                        }
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

                val services = mBluetoothGatt?.services
                services?.map {
                    XLog.info("service uuid : ${it.uuid} , type:${it.type}")
                    it.characteristics?.map { characteristic ->
                        if (characteristic.uuid.toString() == UUID_C) {
                            runOnUiThread {
                                sendMessageBtn.isEnabled = true
                            }
                            doAsync {
                                XLog.info("test read characteristic")
                                mBluetoothGatt?.readCharacteristic(characteristic)
                            }
                            mBluetoothCharacteristic = characteristic
                        }
                    }
                }

            }

            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    XLog.info("character read uuid:${characteristic?.uuid} ${characteristic?.value?.toString()}")
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
                XLog.info("character change.... uuid:${characteristic?.uuid} value:${characteristic?.value?.toString()}")
            }
        })
    }
}
