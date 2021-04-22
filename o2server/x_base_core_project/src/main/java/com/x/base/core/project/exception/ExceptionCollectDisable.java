package com.x.base.core.project.exception;

public class ExceptionCollectDisable extends LanguagePromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public static String defaultMessage = "禁用云服务.";

	public ExceptionCollectDisable() {
		super(defaultMessage);
	}
}
