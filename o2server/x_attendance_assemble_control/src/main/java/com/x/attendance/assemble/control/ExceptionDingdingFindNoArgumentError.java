package com.x.attendance.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingdingFindNoArgumentError extends PromptException {
    public ExceptionDingdingFindNoArgumentError() {
        super("没有传入正确的参数");
    }
}
