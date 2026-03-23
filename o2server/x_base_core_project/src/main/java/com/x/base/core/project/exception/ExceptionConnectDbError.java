package com.x.base.core.project.exception;

public class ExceptionConnectDbError extends PromptException {

	private static final long serialVersionUID = 8121998765154409958L;

	public ExceptionConnectDbError() {
		super("连接到数据库异常，请联系管理员.");
	}
}
