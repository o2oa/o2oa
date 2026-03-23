package com.x.teamwork.assemble.control.jaxrs.config;

import com.x.base.core.project.exception.PromptException;

class ExceptionConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigNotExists( String id ) {
		super("指定的设置信息不存在.ID:" + id );
	}
}
