package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.exception.PromptException;

class ViewInfoFormIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ViewInfoFormIdEmptyException() {
		super("表单ID[formId]为空,无法进行数据保存。" );
	}
}
