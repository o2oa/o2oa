package com.x.query.core.express.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableInsert extends LanguagePromptException {

    ExceptionDisableInsert() {
        super("insert statement is disabled.");
    }
}
