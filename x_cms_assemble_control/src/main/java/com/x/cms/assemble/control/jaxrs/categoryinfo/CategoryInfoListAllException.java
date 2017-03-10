package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoListAllException( Throwable e ) {
		super("查询所有分类信息对象时发生异常。", e );
	}
}
