package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 3/3/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNoCode extends PromptException {

    private static final long serialVersionUID = -3068848622774913903L;

    public ExceptionNoCode() {
        super("微信code不能为空");
    }
}
