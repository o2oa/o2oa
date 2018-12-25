package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSercretaryConfigLeaderIdentityEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSercretaryConfigLeaderIdentityEmpty() {
		super("代理领导身份为空，无法继续进行查询或者保存操作。");
	}
}
