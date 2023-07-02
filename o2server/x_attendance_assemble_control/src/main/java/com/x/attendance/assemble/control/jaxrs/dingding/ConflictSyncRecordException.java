package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

public class ConflictSyncRecordException extends PromptException {


    private static final long serialVersionUID = 7077761236199564642L;

    public ConflictSyncRecordException() {
        super("同步时间和历史有冲突！");
    }
}
