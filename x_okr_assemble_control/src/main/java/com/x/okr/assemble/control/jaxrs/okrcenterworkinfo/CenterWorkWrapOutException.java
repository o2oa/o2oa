package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkWrapOutException( Throwable e ) {
		super("将中心工作查询结果转换为可以输出的数据信息时发生异常。", e );
	}
}
