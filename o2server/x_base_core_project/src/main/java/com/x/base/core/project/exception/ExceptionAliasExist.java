package com.x.base.core.project.exception;

public class ExceptionAliasExist extends LanguagePromptException {

	private static final long serialVersionUID = -7788422876292984022L;

	public ExceptionAliasExist(String str) {
		super("别名:{},已存在.", str);
	}
}
