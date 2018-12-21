package com.x.strategydeploy.assemble.control.keywork.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionKeyWorkInfoIdEmpty extends PromptException {
	private static final long serialVersionUID = -3558611673723866310L;

	public ExceptionKeyWorkInfoIdEmpty() {
		super("重点工作 ID 为空。");
	}
}
