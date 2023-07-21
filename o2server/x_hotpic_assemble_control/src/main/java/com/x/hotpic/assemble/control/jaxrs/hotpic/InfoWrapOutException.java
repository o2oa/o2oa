package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoWrapOutException( Throwable e  ) {
		super("系统将查询结果转换为可输出的数据信息时发生异常。" , e );
	}
}
