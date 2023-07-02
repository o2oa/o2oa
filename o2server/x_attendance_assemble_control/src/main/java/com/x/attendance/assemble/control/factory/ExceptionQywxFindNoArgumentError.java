package com.x.attendance.assemble.control.factory;

import com.x.base.core.project.exception.PromptException;

class ExceptionQywxFindNoArgumentError extends PromptException {
    public ExceptionQywxFindNoArgumentError() {
        super("没有传入正确的参数");
    }
}
