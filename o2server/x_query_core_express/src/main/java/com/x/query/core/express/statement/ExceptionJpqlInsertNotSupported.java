package com.x.query.core.express.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJpqlInsertNotSupported extends LanguagePromptException {

    ExceptionJpqlInsertNotSupported() {
        super("jpql not support insert statement.");
    }
}
