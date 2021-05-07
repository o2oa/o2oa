package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 3/3/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionGetAccessTokenFail extends PromptException {


    private static final long serialVersionUID = 8183666222562959262L;

    public ExceptionGetAccessTokenFail() {
        super("获取微信accessToken失败！");
    }
}
