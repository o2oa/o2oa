package com.x.program.center.jaxrs.apppack;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 6/15/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionFileNotExist extends LanguagePromptException {

    private static final long serialVersionUID = -8045803301464102031L;

    ExceptionFileNotExist(String id){ super(StringUtils.isBlank(id) ? "文件不存在！" : "id: "+id+" 的文件不存在！"); }
}
