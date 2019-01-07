package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service

import android.annotation.TargetApi
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service.CollectService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.APILogData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File


/**
 * 日志收集定时任务
 * 每天执行一次，连接wifi状态下运行
 * Created by fancyLou on 11/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class CollectLogJobService : JobService() {

    override fun onStopJob(params: JobParameters?): Boolean {
        XLog.info("onStopJob:" + params?.jobId)
        return false//true 表示异常结束的任务，重新安排任务 false是完成任务
    }

    private var collectService: CollectService? = null

    override fun onStartJob(params: JobParameters?): Boolean {
        collectService = RetrofitClient.instance().collectApi()
        XLog.info("onStartJob :" + params?.jobId)
        executeTask(params)
        return false//true还在执行,任务执行完成后需要手动调用jobFinished， false 表示任务执行完了
    }

    private fun executeTask(params: JobParameters?) {
        val logFolderPath = FileExtensionHelper.getXBPMLogFolder()
        XLog.info("log folder: $logFolderPath")
        val logFolder = File(logFolderPath)
        if (logFolder.exists()) {
            XLog.info("日志文件夹存在")
            logFolder.listFiles()?.map { file ->
                if (file != null && file.isFile) {
                    if (file.extension == "log") {
                        val fileDate = DateHelper.convertStringToDate("yyyy-MM-dd", file.name.substringBeforeLast(".", ""))
                        if (fileDate != null) {
                            try {
                                if (DateHelper.isToday(fileDate)) {
                                    readTodayLog(file)
                                } else {
                                    readBeforeTodayLog(file)
                                }
                            } catch (e: Exception) {
                                XLog.error("读取日志文件异常", e)
                            }
                        } else {
                            XLog.info("文件名的格式不正确 , fileName: ${file.name}")
                        }
                    } else {
                        XLog.info("其他扩展名的文件不处理, fileName: ${file.name}")
                    }
                }
            }
        } else {
            XLog.error("log folder not exist!!!!!")
        }
        jobFinished(params, false)
    }

    /**
     * 处理以前的日志文件 直接重命名日志文件
     * 然后读取文件内容 发送的服务器上
     *
     */
    private fun readBeforeTodayLog(file: File?) {
        if (file!=null) {
            val date = file.nameWithoutExtension
            val fileCp = File(file.absolutePath + "_cp")
            file.renameTo(fileCp)
            val lines = fileCp.readLines()
            XLog.info("读取以前的日志文件，文件名：${fileCp.name}, 行数：${lines.size}")
            generateLogData(lines, date)
        }
    }

    /**
     * 处理当天的日志， 因为当天的日志还在继续生产内容 所以先复制一份
     * 复制一份文件名： 原文件名_cp 如 2018-06-06.log -> 2018-06-06.log_cp
     * 然后读取这个cp文件的内容，发送到服务器上
     */
    private fun readTodayLog(file: File?) {
        if (file != null) {
            val date = file.nameWithoutExtension
            val fileCp = File(file.absolutePath + "_cp")
            FileUtil.copyFileWithFileChannel(file, fileCp)
            val lines = fileCp.readLines()
            XLog.info("读取今天的日志文件，文件名：${fileCp.name}, 行数：${lines.size}")
            generateLogData(lines, date)
        }
    }


    private fun generateLogData(lines: List<String>, date: String) {
        val logData = APILogData()
        val pref = O2SDKManager.instance().prefs()
        logData.unit = pref.getString(O2.PRE_BIND_UNIT_ID_KEY, "")
        logData.unitName = pref.getString(O2.PRE_BIND_UNIT_KEY, "")
        logData.centerHost = pref.getString(O2.PRE_CENTER_HOST_KEY, "")
//        val centerHttpProtocol = pref.getString(O2.PRE_CENTER_HTTP_PROTOCOL_KEY, "")
        logData.centerContext = pref.getString(O2.PRE_CENTER_CONTEXT_KEY, "")
        logData.centerPort = pref.getInt(O2.PRE_CENTER_PORT_KEY, -1).toString()
        logData.deviceToken = pref.getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")
        logData.distinguishedName = O2SDKManager.instance().distinguishedName
        logData.name = O2SDKManager.instance().cName
        logData.mobile = O2SDKManager.instance().cMobile
        logData.o2Version = AndroidUtils.getAppVersionName(applicationContext)
        logData.osType = O2.DEVICE_TYPE
        logData.osVersion = AndroidUtils.getDeviceOsVersion()
        logData.osCpu = AndroidUtils.getDeviceCpuABI()
        logData.osMemory = AndroidUtils.getDeviceMemory(applicationContext)
        logData.osDpi = pref.getString(O2.PRE_DEVICE_DPI_KEY, "")
        logData.androidManufacturer = AndroidUtils.getDeviceManufacturer()
        logData.manufacturerOsVersion = AndroidUtils.getDeviceBrand() + "/" + AndroidUtils.getDeviceModelNumber()
        logData.logDate = date
        val content = lines.joinToString("\n")
        logData.logContent = content
        if (collectService!=null) {
            collectService?.collectLog(logData)?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.o2Subscribe {
                onNext { res->
                    XLog.info("日志收集返回结果："+res.data?.isValue)
                }
                onError { e, _ ->
                    XLog.error("日志收集异常", e)
                }
            }
        }else{
            XLog.error("云服务器连接失败！！！！")
        }





    }


}