package com.x.attendance.assemble.control.exception;

import com.x.base.core.project.exception.PromptException;

public class DingDingRequestException extends PromptException {

    private static final long serialVersionUID = -2160589718239895222L;

    public DingDingRequestException(String errorMsg) {
        super(errorMsg);
    }
}
