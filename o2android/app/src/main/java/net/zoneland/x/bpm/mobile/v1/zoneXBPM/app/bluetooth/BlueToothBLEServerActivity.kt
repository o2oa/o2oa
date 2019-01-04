package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bluetooth

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.support.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_blue_tooth_bleserver.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import java.util.*


class BlueToothBLEServerActivity : BaseMVPActivity<BlueToothContract.View, BlueToothContract.Presenter>(), BlueToothContract.View {

    override var mPresenter: BlueToothContract.Presenter = BlueToothPresenter()
    override fun layoutResId(): Int = R.layout.activity_blue_tooth_bleserver

    val REQUEST_ENABLE_BT = 1001
    val UUID_C = "00000000-2527-eef3-ffff-ffffe3160865"
    val UUID_SERVER = "00000000-2527-eef3-ffff-ffffe3123865"


    private var mBluetoothManager: BluetoothManager? = null
    private var mBlueAdapter: BluetoothAdapter? = null
    private var bluetoothGattServer: BluetoothGattServer? = null


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("服务端")
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) { //是否支持蓝牙BLE
            XToast.toastShort(this, "设备不支持蓝牙BLE功能！")
            finish()
        }

        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBlueAdapter = mBluetoothManager?.adapter

        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bluetoothGattServer?.close()
        }catch (e:Exception){}
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun start() {
        if (mBlueAdapter != null) {
            if (mBlueAdapter?.isEnabled != true) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                initGATTServer()
            }
        } else {
            messageTv.text = "没有获取到蓝牙适配器。。。。"
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initGATTServer() {
        val settings: AdvertiseSettings = AdvertiseSettings.Builder()
                .setConnectable(true)
                .build()

        val advertiseData: AdvertiseData = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(true)
                .build()

        val scanResponseData: AdvertiseData = AdvertiseData.Builder()
                .addServiceUuid(ParcelUuid(UUID.fromString(UUID_SERVER)))
                .setIncludeTxPowerLevel(true)
                .build()


        val callback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                XLog.info("BLE advertisement added successfully")
                showText("1. initGATTServer success")
                XLog.info("1. initGATTServer success")
                initServices(getContext())
            }

            override fun onStartFailure(errorCode: Int) {
                XLog.error("Failed to add BLE advertisement, reason: " + errorCode)
                showText("1. initGATTServer failure")
            }
        }
        mBlueAdapter?.bluetoothLeAdvertiser?.startAdvertising(settings, advertiseData, scanResponseData, callback)
    }

    private fun initServices(context: Context) {
        bluetoothGattServer = mBluetoothManager?.openGattServer(context, bluetoothGattServerCallback)
        val service = BluetoothGattService(UUID.fromString(UUID_SERVER), BluetoothGattService.SERVICE_TYPE_PRIMARY)
        //add a write characteristic.
        val characteristicWrite = BluetoothGattCharacteristic(UUID.fromString(UUID_C),
                BluetoothGattCharacteristic.PROPERTY_WRITE or
                        BluetoothGattCharacteristic.PROPERTY_READ or
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE)
        service.addCharacteristic(characteristicWrite)

        bluetoothGattServer?.addService(service)
        XLog.info("2. initServices ok")
        showText("2. initServices ok")
    }

    private val bluetoothGattServerCallback: BluetoothGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            XLog.info(String.format("1.onConnectionStateChange：device name = %s, address = %s", device?.name ?: "未知", device?.address))
            XLog.info(String.format("1.onConnectionStateChange：status = %s, newState =%s ", status, newState))
            super.onConnectionStateChange(device, status, newState)
        }


        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
            XLog.info(String.format("onServiceAdded：status = %s", status))
        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice?, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic?) {
//            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            XLog.info(String.format("onCharacteristicReadRequest：device name = %s, address = %s", device?.name ?: "未知", device?.address))
            XLog.info(String.format("onCharacteristicReadRequest：requestId = %s, offset = %s", requestId, offset))
            bluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic?.value)
        }

        override fun onCharacteristicWriteRequest(device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            XLog.info(String.format("3.onCharacteristicWriteRequest：device name = %s, address = %s", device?.name ?: "未知", device?.address))
            XLog.info(String.format("3.onCharacteristicWriteRequest：requestId = %s, preparedWrite=%s, responseNeeded=%s, offset=%s, value=%s", requestId, preparedWrite, responseNeeded, offset, value?.toString()));
            bluetoothGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
            //4.处理响应内容
            onResponseToClient(value, device, requestId, characteristic)

        }
    }

    /**
     * 响应客户端发送过来的信息
     */
    private fun onResponseToClient(value: ByteArray?, device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?) {
        XLog.info(String.format("4.onResponseToClient：device name = %s, address = %s", device?.name ?: "未知", device?.address))
        XLog.info(String.format("4.onResponseToClient：requestId = %s", requestId))
        showText(value?.toString() ?: "")
    }


    private fun showText(text: String) {
        runOnUiThread {
            messageTv.text = text
        }
    }


}
