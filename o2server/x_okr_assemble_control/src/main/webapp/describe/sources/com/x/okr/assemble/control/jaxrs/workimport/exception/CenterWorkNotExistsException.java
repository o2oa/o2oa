package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class CenterWorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkNotExistsException( String id ) {
		super("指定ID的中心工作记录不存在。ID：" + id );
	}
}
