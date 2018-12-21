package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionTaskEventCanNotRepeatMaster extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionTaskEventCanNotRepeatMaster( String id ) {
		super("任务事件不允许进行重复，请不要进行重复规则设置.REPEATMASTERID:" + id);
	}
}
