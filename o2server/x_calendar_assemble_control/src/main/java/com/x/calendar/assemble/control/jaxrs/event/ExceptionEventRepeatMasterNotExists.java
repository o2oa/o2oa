package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventRepeatMasterNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventRepeatMasterNotExists( String masterId ) {
		super("指定的日历事件重复宿主信息不存在.MASTERID:" + masterId );
	}
}
