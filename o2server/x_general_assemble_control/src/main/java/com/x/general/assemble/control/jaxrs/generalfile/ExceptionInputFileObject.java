package com.x.general.assemble.control.jaxrs.generalfile;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInputFileObject extends PromptException {

    private static final long serialVersionUID = 9085364457175859374L;

    ExceptionInputFileObject(String flag) {
        super("对象不存在:{}.", flag);
    }

}