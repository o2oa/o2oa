package com.x.base.core.project.exception;

public class ExceptionCollectConnectError extends LanguagePromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public static String defaultMessage = "云服务器连接错误.";

	public ExceptionCollectConnectError() {
		super(defaultMessage);
	}
}
