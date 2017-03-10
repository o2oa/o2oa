package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoIdEmptyException() {
		super("分类信息ID为空，无法继续查询数据。" );
	}
}
