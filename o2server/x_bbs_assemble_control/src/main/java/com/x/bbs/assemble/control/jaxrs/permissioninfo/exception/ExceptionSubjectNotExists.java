package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectNotExists( String id ) {
		super("指定ID的主题信息不存在.Id:" + id );
	}
}
