package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoWrapInException( Throwable e  ) {
		super("系统将输入的数据转换为一个热点信息对象时发生异常。" , e );
	}
}
