package com.x.query.assemble.surface.jaxrs.search;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryEmpty extends LanguagePromptException {

    ExceptionQueryEmpty() {
        super("搜索条件不能为空.");
    }

}
