package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 3/3/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNoConfigArguments extends PromptException {

    private static final long serialVersionUID = 1151495856256067183L;

    public ExceptionNoConfigArguments(String error) {
        super(error);
    }
}
