package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryFormIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryFormIdEmpty() {
		super("文档所在分类未设置编辑表单或者编辑表单已被清除，无法进行查询或者保存操作。" );
	}
}
