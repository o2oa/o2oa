package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInvalidServerAddress extends PromptException {

    private static final long serialVersionUID = -1367282830558670567L;

    ExceptionInvalidServerAddress(String ip) {
        super("代理ip：{}不符合规则，请联系管理员.", ip);
    }
}
