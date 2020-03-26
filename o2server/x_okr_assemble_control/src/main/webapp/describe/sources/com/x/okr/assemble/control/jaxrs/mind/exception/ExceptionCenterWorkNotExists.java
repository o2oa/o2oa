package com.x.okr.assemble.control.jaxrs.mind.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCenterWorkNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCenterWorkNotExists( String id ) {
		super("指定ID的中心工作不存在!ID:" + id );
	}
}
