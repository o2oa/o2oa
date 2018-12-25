package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventNotExists( String id ) {
		super("指定的日历事件信息不存在.ID:" + id );
	}
}
