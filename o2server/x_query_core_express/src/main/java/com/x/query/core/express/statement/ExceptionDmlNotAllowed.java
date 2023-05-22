package com.x.query.core.express.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDmlNotAllowed extends LanguagePromptException {

    ExceptionDmlNotAllowed() {
        super("statement not allowed.");
    }
}
