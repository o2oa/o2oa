package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class CategoryFormIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CategoryFormIdEmptyException() {
		super("文档所在分类未设置编辑表单或者编辑表单已被清除，无法进行查询或者保存操作。" );
	}
}
