package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.content.Context
import android.util.Log

/**
 * 日志记录器 单例
 * Created by fancyLou on 11/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

class LogSingletonService private constructor(){
    companion object {
        private var instance: LogSingletonService? = null
        fun instance():LogSingletonService{
            if (instance==null){
                instance = LogSingletonService()
            }
            return instance!!
        }
    }
    private lateinit var mContext: Context

    private val executor by lazy { LogThreadPoolExecutor() }

    /**
     * Application onCreate 的时候注册
     */
    fun registerApp(context: Context) {
        mContext = context
    }

    /**
     * 记录日志
     */
    fun recordLog(tag:String, time:String, level:String, log:String, t: Throwable? = null){
        executor.execute(LogRecord2FileTask(mContext, time, tag, level, log, t))
    }


}