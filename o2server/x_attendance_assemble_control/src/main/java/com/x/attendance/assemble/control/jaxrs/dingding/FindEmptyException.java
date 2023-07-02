package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

public class FindEmptyException  extends PromptException {


    private static final long serialVersionUID = 4335298261104588212L;

    public FindEmptyException() {
        super("没有找到数据！");
    }
}
