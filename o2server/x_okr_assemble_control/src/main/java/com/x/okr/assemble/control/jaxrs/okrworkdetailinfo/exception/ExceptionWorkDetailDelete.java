package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkDetailDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkDetailDelete( Throwable e, String id ) {
		super("系统在删除工作详细信息时发生异常. ID:" + id, e );
	}
}
