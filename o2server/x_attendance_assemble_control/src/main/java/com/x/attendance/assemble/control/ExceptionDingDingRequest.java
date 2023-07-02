package com.x.attendance.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingDingRequest extends PromptException {

    private static final long serialVersionUID = -2160589718239895222L;

    public ExceptionDingDingRequest(String errorMsg) {
        super(errorMsg);
    }
}
