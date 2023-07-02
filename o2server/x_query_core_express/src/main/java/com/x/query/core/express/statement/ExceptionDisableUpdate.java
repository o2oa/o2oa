package com.x.query.core.express.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDisableUpdate extends LanguagePromptException {

    ExceptionDisableUpdate() {
        super("update statement is disabled.");
    }
}
