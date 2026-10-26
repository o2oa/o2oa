package com.x.query.core.express.statement;

import com.x.base.core.project.exception.PromptException;

class ExceptionSqlUnsafe extends PromptException {

    private static final long serialVersionUID = 1L;

    ExceptionSqlUnsafe(String detail) {
        super("sql校验失败:{}", detail);
    }
}
