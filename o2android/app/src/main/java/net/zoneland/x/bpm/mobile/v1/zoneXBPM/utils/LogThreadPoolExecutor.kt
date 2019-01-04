package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * 日志存储线程池
 * Created by fancyLou on 11/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class LogThreadPoolExecutor: ThreadPoolExecutor {

    companion object {
        private const val MAX_POOL_SIZE = 256
        private const val KEEP_ALIVE = 1L
        /**
         * 创建线程工厂
         */
        private val sThreadFactory = ThreadFactory { runnable -> Thread(runnable, "logRecord" ) }
        /**
         * 线程队列方式 先进先出
         */
        private val FIFO = Comparator<Runnable> { lhs, rhs ->
            if (lhs is LogRecord2FileTask && rhs is LogRecord2FileTask) {
                lhs.time.compareTo(rhs.time)
            } else {
                0
            }
        }
    }


    constructor(poolSize: Int = 5):super(poolSize, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, PriorityBlockingQueue<Runnable>(MAX_POOL_SIZE, FIFO), sThreadFactory)
}