package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionMediaTypeNotSupport extends LanguagePromptException {

    private static final long serialVersionUID = 4862362281353270832L;

    ExceptionMediaTypeNotSupport() {
        super("素材类型不支持，目前只支持 image、voice、video thumb 类型.");
    }
}
