package com.x.okr.assemble.control.jaxrs.okrconfigworklevel.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkLevelConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkLevelConfigNotExists( String id ) {
		super("指定ID的工作级别配置不存在。ID:" + id );
	}
}
