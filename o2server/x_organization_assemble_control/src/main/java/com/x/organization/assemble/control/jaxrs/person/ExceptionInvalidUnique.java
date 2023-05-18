package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidUnique extends LanguagePromptException {

    private static final long serialVersionUID = -1792144070153264245L;

    ExceptionInvalidUnique(String name) {
       super("唯一编码不能使用保留字串,且不能使用特殊字符:{}.", name);
   }
}
