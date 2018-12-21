package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionTopUnitNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionTopUnitNotExists( String name ) {
		super("顶层组织信息不存在！TopUnit:" + name );
	}
}
