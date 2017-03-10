package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;

import com.x.base.core.exception.PromptException;

class WorkDetailDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDetailDeleteException( Throwable e, String id ) {
		super("系统在删除工作详细信息时发生异常. ID:" + id, e );
	}
}
