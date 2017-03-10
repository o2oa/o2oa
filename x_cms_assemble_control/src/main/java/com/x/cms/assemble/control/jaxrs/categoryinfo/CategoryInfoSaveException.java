package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoSaveException( Throwable e ) {
		super("分类信息在保存时发生异常.", e );
	}
}
