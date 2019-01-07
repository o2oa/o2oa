package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.util.Log;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.BuildConfig;

import org.jetbrains.annotations.NotNull;


/**
 * 日志
 * Created by FancyLou on 2016/11/14.
 */

public final class XLog {


    private static Printer printer = new Printer();

    private XLog() {
    }


    public static Printer tag(String tag) {
        printer.tag(tag);
        return printer;
    }

    public static void verbose(String message) {
        printer.verbose(message);
    }

    public static void warn(String message) {
        printer.warn(message);
    }

    public static void info(String message) {
        printer.info(message);
    }

    public static void debug(String message) {
        printer.debug(message);
    }

    public static void error(String message) {
        printer.error(message);
    }

    public static void error(String message, Throwable throwable) {
        printer.error(message, throwable);
    }

}

final class Printer {
    private static final String DEFAULT_TAG = "O2";

    private boolean isEnableLog() {
        return BuildConfig.LOG_ENABLE;
    }

    private boolean isEnableLogFile() {
        return BuildConfig.LOG_FILE;
    }

    private String tag;

    public Printer() {
        tag = DEFAULT_TAG;
    }

    public Printer tag(String tag) {
        this.tag = tag;
        return this;
    }


    public void warn(String message) {
        if (!isEnableLog()) {
            return;
        }
        if (isEnableLog()) {
            String log = logBeautify(message);
            Log.w(tag, log);
        }
    }

    public void verbose(String message) {
        if (!isEnableLog()) {
            return;
        }
        if (isEnableLog()) {
            String log = logBeautify(message);
            Log.v(tag, log);
        }
    }

    public void debug(String message) {
        if (!isEnableLog()) {
            return;
        }
        if (isEnableLog()) {
            String log = logBeautify(message);
            Log.d(tag, log);
        }
    }

    public void info(String message) {
        if (!isEnableLog() && !isEnableLogFile()) {
            return;
        }
        String log = logBeautify(message);
        if (isEnableLog()) {
            Log.i(tag, log);
        }
        if (isEnableLogFile()) {
            recordLog2File(Log.INFO, log);
        }
    }

    public void error(String message) {
        String log = logBeautify(message);
        Log.e(tag, log);
        if (isEnableLogFile()) {
            recordLog2File(Log.ERROR, log);
        }
    }

    public void error(String message, Throwable throwable) {
        String log = logBeautify(message);
        Log.e(tag, log, throwable);
        if (isEnableLogFile()) {
            recordLog2FileError(Log.ERROR, log, throwable);
        }
    }


    /**
     * 日志记录到文件
     * @param level
     * @param log
     */
    private void recordLog2File(@NotNull int level, @NotNull String log) {
        recordLog2FileError(level, log, null);
    }

    private void recordLog2FileError(@NotNull int level, @NotNull String log, Throwable throwable) {
        String l = logLevel(level);
        String time = DateHelper.nowByFormate("yyyy-MM-dd HH:mm:ss.SSS");
        LogSingletonService.Companion.instance().recordLog(tag, time, l, log, throwable);
    }


    private String logLevel(int level) {
        String l = "W";
        switch (level) {
            case Log.WARN:
                l = "W";
                break;
            case Log.ASSERT:
                l = "A";
                break;
            case Log.VERBOSE:
                l = "V";
                break;
            case Log.DEBUG:
                l = "D";
                break;
            case Log.INFO:
                l = "I";
                break;
            case Log.ERROR:
                l = "E";
                break;
        }
        return l;
    }

    private String logBeautify(String log) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StringBuffer buffer = new StringBuffer();
        int stackOffset = getStackOffset(trace);
        int printMethodCount = 1; //打印多少层级的方法
        if (printMethodCount + stackOffset > trace.length) {
            printMethodCount = trace.length - stackOffset - 1;
        }
        for (int i = printMethodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            String fileName = trace[stackIndex].getFileName();
            String methodName = trace[stackIndex].getMethodName();
            int lineNumber = trace[stackIndex].getLineNumber();
            buffer.append(getSimpleClassName(trace[stackIndex].getClassName()));
            buffer.append(".");
            buffer.append(methodName);
            buffer.append(" ");
            buffer.append("(");
            buffer.append(fileName);
            buffer.append(":");
            buffer.append(lineNumber);
            buffer.append(") ");
        }
        buffer.append(log);
        return buffer.toString();
    }

    //堆栈最小偏移量
    private static final int MIN_STACK_OFFSET = 3;

    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(Printer.class.getName()) && !name.equals(XLog.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

}
