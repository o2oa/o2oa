package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMsgRevokeOutOfTime extends PromptException {

    public ExceptionMsgRevokeOutOfTime() {
        super("撤回消息超过时限！");
    }
}
