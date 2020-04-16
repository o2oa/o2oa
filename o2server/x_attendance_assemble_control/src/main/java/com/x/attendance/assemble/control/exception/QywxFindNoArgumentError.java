package com.x.attendance.assemble.control.exception;

import com.x.base.core.project.exception.PromptException;

public class QywxFindNoArgumentError extends PromptException {
    public QywxFindNoArgumentError() {
        super("没有传入正确的参数");
    }
}
