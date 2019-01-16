package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service

import android.annotation.TargetApi
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * 清除临时文件的定时任务
 * Created by fancy on 2017/4/20.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class ClearTempFileJobService : JobService() {


    override fun onStartJob(params: JobParameters?): Boolean {
        XLog.info("onStartJob :" + params?.jobId)
        executeTask(params)
        return true//true还在执行,任务执行完成后需要手动调用jobFinished， false 表示任务执行完了
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        XLog.info("onStopJob:" + params?.jobId)
        return false//true 表示异常结束的任务，重新安排任务 false是完成任务
    }

    private fun executeTask(params: JobParameters?) {
        Observable.create<Unit> { subscriber ->
            try {
                val baseFolderPath = FileExtensionHelper.getXBPMBaseFolder()
                XLog.info("ClearTempFile Base Folder:$baseFolderPath")
                val baseFolder = File(baseFolderPath)
                if (baseFolder.exists()) {
                    baseFolder.listFiles { file ->
                        file.isDirectory
                    }.map { folder ->
                        folder.listFiles().map { file ->
                            try {
                                val time = file.lastModified()
                                val sevenDay = 1000 * 60 * 60 * 24 * 7
                                val now = System.currentTimeMillis()
                                if (now - sevenDay > time) {
                                    val filename = file.name
                                    file.delete()
                                    XLog.info("delete success, File:$filename")
                                }
                            } catch(e: Exception) {
                                XLog.error("删除临时文件失败", e)
                            }
                        }
                    }
                }
                subscriber.onNext(Unit)
            } catch(e: Exception) {
                XLog.error("", e)
                subscriber.onError(e)
            }
            subscriber.onCompleted()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ ->
                    XLog.info("onNext finish Job :" + params?.jobId)
                    jobFinished(params, false)
                }, { e ->
                    XLog.error("onError executeTask error", e)
                    jobFinished(params, false)
                }, {})

    }

}