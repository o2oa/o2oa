package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionNotSupportProtocol extends LanguagePromptException {

	private static final long serialVersionUID = -5849117416911160227L;

	public ExceptionNotSupportProtocol(String address) {
		super("该地址访问协议不支持：{}.", address);
	}
}
