package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoDeleteException( Throwable e, String id ) {
		super("分类信息在删除时发生异常。ID:" + id, e );
	}
}
