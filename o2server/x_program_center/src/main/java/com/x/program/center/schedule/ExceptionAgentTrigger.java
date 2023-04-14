package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentTrigger extends LanguagePromptException {

    private static final long serialVersionUID = -8597019540568284908L;

    ExceptionAgentTrigger(Throwable cause, String id, String name, String cron) {
        super(cause, "id:{}, name:{}, cron:{}.", id, name, cron);
    }
}
