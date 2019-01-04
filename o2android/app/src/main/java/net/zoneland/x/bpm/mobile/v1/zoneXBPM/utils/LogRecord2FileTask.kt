package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.Manifest
import android.content.Context
import android.util.Log
import java.io.*


/**
 * Created by fancyLou on 11/06/2018.
 * Copyright © 2018 O2. All rights reserved.
 */


class LogRecord2FileTask(val context: Context,
                         val time: String,
                         val tag: String,
                         val level: String,
                         val log: String,
                         val t: Throwable?) : Runnable {


    override fun run() {
        if (!AndroidUtils.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e("LogRecord2FileTask", "存储文件权限没有开通！")
        } else {
            try {
                val filePath = getLogFile()
                val todayFile = File(filePath)
                if (!todayFile.exists()) {
                    SDCardHelper.generateNewFile(filePath)
                }
                val line = lineSeparator()
                // Encode and encrypt the message.
                val trace = FileOutputStream(todayFile, true)
                val writer = OutputStreamWriter(trace, "utf-8")
                writer.write("$time $level/$tag: $log $line")
                if (t != null) {
                    writer.write(render(t))
                }
                writer.flush()
                trace.flush()
                trace.close()
            } catch (e: Exception) {
                Log.e("LogRecord2FileTask", "", e)
            }

        }
    }


    /**
     * 获取今天的日志文件
     */
    private fun getLogFile(): String {
        return FileExtensionHelper.getXBPMLogFolder() + File.separator + DateHelper.nowByFormate("yyyy-MM-dd") + ".log"
    }

    /**
     * 分行符
     */
    private fun lineSeparator(): String {
        var lineSeparator: String? = System.getProperty("line.separator")
        if (lineSeparator == null) {
            lineSeparator = "\n"
        }
        return lineSeparator
    }

    /**
     * 异常信息格式化
     */
    private fun render(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)

        try {
            throwable.printStackTrace(pw)
        } catch (var6: RuntimeException) {
        }
        pw.flush()
        val reader = LineNumberReader(StringReader(sw.toString()))
        val lines = StringBuffer()
        try {
            var line: String? = reader.readLine()
            while (line != null) {
                lines.append(line)
                lines.append(lineSeparator())
                line = reader.readLine()
            }
        } catch (var7: IOException) {
            if (var7 is InterruptedIOException) {
                Thread.currentThread().interrupt()
            }
            lines.append(var7.toString())
            lines.append(lineSeparator())
        }
        return lines.toString()
    }
}