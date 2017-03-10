package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoUpdateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoUpdateException( Throwable e, String id ) {
		super("分类信息在更新时发生异常.ID:" + id, e );
	}
}
