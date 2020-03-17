package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkTypeConfigNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkTypeConfigNotExists( String id ) {
		super("指定ID的工作类别配置不存在。ID:" + id );
	}
}
