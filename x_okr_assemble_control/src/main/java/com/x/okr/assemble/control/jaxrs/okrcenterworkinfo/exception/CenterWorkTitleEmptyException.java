package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.exception.PromptException;

public class CenterWorkTitleEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CenterWorkTitleEmptyException() {
		super("中心工作[标题]过长,请限制为70个汉字。");
	}
}
