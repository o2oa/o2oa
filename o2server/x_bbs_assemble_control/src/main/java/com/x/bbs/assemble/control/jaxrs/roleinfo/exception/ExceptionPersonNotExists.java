package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonNotExists( String person ) {
		super("人员信息不存在！Person:" + person );
	}
}
