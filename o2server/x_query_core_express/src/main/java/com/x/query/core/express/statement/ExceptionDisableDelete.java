package com.x.query.core.express.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableDelete extends LanguagePromptException {

    ExceptionDisableDelete() {
        super("delete statement is disabled.");
    }
}
