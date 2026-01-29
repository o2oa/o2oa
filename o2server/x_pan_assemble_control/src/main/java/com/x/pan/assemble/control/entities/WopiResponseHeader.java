package com.x.pan.assemble.control.entities;

/**
 * Wopi response header when handles lock
 *
 * @author ethendev
 * @date 2019/10/26
 */
public class WopiResponseHeader {
    public final static String HOST_ENDPOINT = "X-WOPI-HostEndpoint";
    public final static String INVALID_FILE_NAME_ERROR = "X-WOPI-InvalidFileNameError";
    public final static String LOCK = "X-WOPI-Lock";
    public final static String LOCK_FAILURE_REASON = "X-WOPI-LockFailureReason";
    public final static String LOCKED_BY_OTHER_INTERFACE = "X-WOPI-LockedByOtherInterface";
    public final static String MACHINE_NAME = "X-WOPI-MachineName";
    public final static String PREF_TRACE = "X-WOPI-PerfTrace";
    public final static String SERVER_ERROR = "X-WOPI-ServerError";
    public final static String SERVER_VERSION = "X-WOPI-ServerVersion";
    public final static String VALID_RELATIVE_TARGET = "X-WOPI-ValidRelativeTarget";
}
