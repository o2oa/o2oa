package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;

import com.x.base.core.exception.PromptException;

class WorkDetailSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkDetailSaveException( Throwable e ) {
		super("系统在保存工作详细信息时发生异常.", e );
	}
}
