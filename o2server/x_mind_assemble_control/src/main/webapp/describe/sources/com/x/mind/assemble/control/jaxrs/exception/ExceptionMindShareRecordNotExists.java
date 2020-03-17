package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindShareRecordNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindShareRecordNotExists( String id ) {
		super("脑图文件分享信息对象不存在。ID:" + id );
	}
}
