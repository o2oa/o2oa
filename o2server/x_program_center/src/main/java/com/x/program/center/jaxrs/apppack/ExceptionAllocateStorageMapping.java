package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 12/1/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionAllocateStorageMapping extends LanguagePromptException {


    private static final long serialVersionUID = -2318434030336784474L;

    ExceptionAllocateStorageMapping() {
        super("无法分派存储器");
    }
}
