package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRequiredParameterNotPassed extends LanguagePromptException {

    private static final long serialVersionUID = -9089355008820123519L;

    ExceptionRequiredParameterNotPassed(String parameter) {
        super("需要的参数未传递:{}.", parameter);
    }
}
