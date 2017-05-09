package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.exception.PromptException;

public class SercretaryConfigLeaderIdentityEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SercretaryConfigLeaderIdentityEmptyException() {
		super("代理领导身份为空，无法继续进行查询或者保存操作。");
	}
}
