package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAuthorizeTakerIdentityEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionAuthorizeTakerIdentityEmpty() {
		super("工作授权承担者为空，无法继续进行授权操作。");
	}
}
