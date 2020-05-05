package com.x.attendance.assemble.control.jaxrs.qywx;

import com.x.base.core.project.exception.PromptException;

public class SyncDateException extends PromptException {


    private static final long serialVersionUID = -6409463169780597687L;

    public SyncDateException() {
        super("传入的同步时间不正确！");
    }
}
