package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkWrapInException( Throwable e ) {
		super("将用户传入的数据转换为一个中心工作对象信息时发生异常。", e );
	}
}
