package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentWrapOutException( Throwable e ) {
		super("将查询出来的文档信息对象转换为可输出的数据信息时发生异常。", e );
	}
}
