package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class OkrOperationDynamicSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	OkrOperationDynamicSaveException( Throwable e, String id ) {
		super("系统在保存中心工作操作动态信息时发生异常。ID:" + id, e );
	}
	
	OkrOperationDynamicSaveException( Throwable e ) {
		super("系统在保存中心工作操作动态信息时发生异常。", e );
	}
}
