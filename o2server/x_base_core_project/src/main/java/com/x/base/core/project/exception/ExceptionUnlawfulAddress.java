package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionUnlawfulAddress extends LanguagePromptException {

	private static final long serialVersionUID = -3709552818045153245L;

	public ExceptionUnlawfulAddress(String address) {
		super("该接口的服务地址不在白名单内：{}.", address);
	}
}
