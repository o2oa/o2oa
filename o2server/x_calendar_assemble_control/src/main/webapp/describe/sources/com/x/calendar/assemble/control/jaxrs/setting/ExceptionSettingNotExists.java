package com.x.calendar.assemble.control.jaxrs.setting;

import com.x.base.core.project.exception.PromptException;

class ExceptionSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionSettingNotExists( String id ) {
		super("指定的设置信息不存在.ID:" + id );
	}
}
