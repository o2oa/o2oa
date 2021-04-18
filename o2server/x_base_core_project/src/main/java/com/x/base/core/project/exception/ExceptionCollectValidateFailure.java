package com.x.base.core.project.exception;

public class ExceptionCollectValidateFailure extends LanguagePromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public static String defaultMessage = "云服务器认证失败.";

	public ExceptionCollectValidateFailure() {
		super(defaultMessage);
	}
}
