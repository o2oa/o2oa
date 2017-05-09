package com.x.cms.assemble.control.jaxrs.categoryinfo.exception;

import com.x.base.core.exception.PromptException;

public class CategoryInfoAppIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CategoryInfoAppIdEmptyException() {
		super("分类信息所属应用栏目ID为空，无法继续查询数据。" );
	}
}
