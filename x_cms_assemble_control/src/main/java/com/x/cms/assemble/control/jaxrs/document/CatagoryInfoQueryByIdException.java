package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class CategoryInfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoQueryByIdException( Throwable e, String id ) {
		super("根据ID查询分类信息对象时发生异常。ID:"+id, e );
	}
}
