package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUnitNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUnitNotExists( String unitName ) {
		super("组织信息不存在！Unit:" + unitName );
	}
}
