package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionCreateMenuError extends LanguagePromptException {

    private static final long serialVersionUID = 4862362281353270832L;

    ExceptionCreateMenuError(Integer code , String err) {
        super("创建菜单错误code :{} message：{}.", code, err);
    }
}
