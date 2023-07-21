package com.x.base.core.project.exception;

public class JsonElementConvertToWrapInException extends PromptException {

	private static final long serialVersionUID = -4391403166633224425L;

	public JsonElementConvertToWrapInException(Exception e, Class<?> clz) {
		super("can not convert jsonElement to class:{}, because:{}.", clz.getName(), e.getMessage());
	}
}
