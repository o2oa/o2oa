package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionQywxNoWorkUrl extends LanguagePromptException {


    ExceptionQywxNoWorkUrl() {
        super("企业微信没有配置打开工作的URL.");
    }
}
