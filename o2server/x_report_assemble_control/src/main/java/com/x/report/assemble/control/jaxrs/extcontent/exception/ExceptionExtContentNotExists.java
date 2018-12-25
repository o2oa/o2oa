package com.x.report.assemble.control.jaxrs.extcontent.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionExtContentNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionExtContentNotExists( String id ) {
		super("指定的扩展信息不存在.ID:" + id );
	}
}
