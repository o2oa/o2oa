package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInitialManagerName extends LanguagePromptException {

    private static final long serialVersionUID = 4132300948670472899L;

    ExceptionInitialManagerName() {
        super("不能使用初始管理员标识.");
    }
}
