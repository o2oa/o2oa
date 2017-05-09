package com.x.cms.assemble.control.jaxrs.view.exception;

import com.x.base.core.exception.PromptException;

public class ViewInfoAppIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ViewInfoAppIdEmptyException() {
		super("栏目名称[appId]为空,无法进行数据保存。" );
	}
}
