package com.x.query.core.express.statement;

import com.x.base.core.project.exception.PromptException;

class ExceptionFilterInvalid extends PromptException {

    ExceptionFilterInvalid(String field, String value) {
        super("filter entry {} invalid:{}.", field, value);
    }
}
