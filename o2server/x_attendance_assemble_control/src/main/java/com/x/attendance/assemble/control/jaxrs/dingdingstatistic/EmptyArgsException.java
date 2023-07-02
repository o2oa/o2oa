package com.x.attendance.assemble.control.jaxrs.dingdingstatistic;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 2020-04-07.
 * Copyright © 2020 O2. All rights reserved.
 */
public class EmptyArgsException extends PromptException {
    public EmptyArgsException() {
        super("传入参数不能为空！");
    }
}
