package com.x.base.core.project.exception;

public class ExceptionStorageMappingNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static final String defaultMessage = "标识为:{}的存储不存在.";

	public ExceptionStorageMappingNotExist(String storage) {
		super(defaultMessage, storage);
	}

}
