package com.x.attendance.assemble.control.jaxrs.dingding.exception;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 2020-04-01.
 * Copyright © 2020 O2. All rights reserved.
 */
public class TimeEmptyException extends PromptException {

    public TimeEmptyException() {
        super("传入的时间参数不正确！");
    }
}
