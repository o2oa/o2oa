package com.x.base.core.project.exception;

public class ExceptionNameExist extends LanguagePromptException {

	private static final long serialVersionUID = -283505161497831794L;

	public ExceptionNameExist(String str) {
		super("名称:{},已存在.", str);
	}
}
