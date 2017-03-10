package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoAppIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoAppIdEmptyException() {
		super("分类信息所属应用栏目ID为空，无法继续查询数据。" );
	}
}
