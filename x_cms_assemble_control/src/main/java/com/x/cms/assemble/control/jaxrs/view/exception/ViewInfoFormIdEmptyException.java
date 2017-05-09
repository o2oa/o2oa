package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.exception.PromptException;

public class ViewInfoFormIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ViewInfoFormIdEmptyException() {
		super("表单ID[formId]为空,无法进行数据保存。" );
	}
}
