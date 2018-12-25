package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionViewDateQueryCategoryIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionViewDateQueryCategoryIdEmpty() {
		super("列表数据查询条件[categoryId]为空,无法进行数据查询。" );
	}
}
