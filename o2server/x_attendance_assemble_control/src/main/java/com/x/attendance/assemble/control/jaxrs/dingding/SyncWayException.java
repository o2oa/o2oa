package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

public class SyncWayException extends PromptException {

    private static final long serialVersionUID = -6072567404702590349L;
    public SyncWayException() {
        super("传入的同步时间不正确！");
    }
}
