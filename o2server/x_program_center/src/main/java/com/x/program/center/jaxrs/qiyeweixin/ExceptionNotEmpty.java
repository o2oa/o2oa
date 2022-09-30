package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNotEmpty extends LanguagePromptException {


    ExceptionNotEmpty(String name) {
        super("{} 不能为空.", name);
    }
}
