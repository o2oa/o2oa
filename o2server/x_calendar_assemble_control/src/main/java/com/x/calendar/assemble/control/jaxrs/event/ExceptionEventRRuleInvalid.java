package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventRRuleInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventRRuleInvalid( String rule ) {
		super("日程事件重复规则不合法。RRule:" + rule);
	}
}
