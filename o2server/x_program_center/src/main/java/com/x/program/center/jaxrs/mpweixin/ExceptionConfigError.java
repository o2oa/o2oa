package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/3/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionConfigError extends LanguagePromptException {

    private static final long serialVersionUID = -3068848622774913903L;

    public ExceptionConfigError() {
        super("微信公众号配置文件未启用");
    }
}
