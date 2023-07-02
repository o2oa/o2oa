package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNoPackInfo extends LanguagePromptException {

    private static final long serialVersionUID = -4443630173583290883L;

    ExceptionNoPackInfo() {
        super("当前O2云账号没有历史打包信息！");
    }
}
