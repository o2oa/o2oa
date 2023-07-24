package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

public class MoreThanSevenDayException extends PromptException {


    private static final long serialVersionUID = 7077761236199564642L;

    public MoreThanSevenDayException() {
        super("同步时间间隔大于7天！");
    }
}
