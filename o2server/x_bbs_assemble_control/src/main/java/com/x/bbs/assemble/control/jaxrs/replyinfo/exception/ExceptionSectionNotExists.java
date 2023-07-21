package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionNotExists( String id ) {
		super("指定ID的版块信息不存在.ID:" + id );
	}
}
