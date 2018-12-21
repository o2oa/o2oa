package com.x.calendar.assemble.control.jaxrs.event;

import com.x.base.core.project.exception.PromptException;

class ExceptionEventPropertyEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEventPropertyEmpty( String title ) {
		super("事件["+ title +"]属性为空，数据操作异常。");
	}
}
