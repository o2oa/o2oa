package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 3/2/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionMPWeixinAccessToken extends PromptException {

    private static final long serialVersionUID = 5430065198687029845L;

    public ExceptionMPWeixinAccessToken(Integer code, String message) {
        super("获取微信公众号  access token 失败,错误代码:{}, 错误消息:{}.", new Object[]{code, message});
    }
}
