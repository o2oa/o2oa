package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionFileTypeError extends PromptException {

	private static final long serialVersionUID = 7072007208242949241L;
	public static final String defaultMessage = "不被允许的文件类型.";

	public ExceptionFileTypeError() {
		super(defaultMessage);
	}

}
