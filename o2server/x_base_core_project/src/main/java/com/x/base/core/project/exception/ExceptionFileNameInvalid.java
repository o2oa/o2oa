package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionFileNameInvalid extends PromptException {

	private static final long serialVersionUID = -283505161497831794L;

	public ExceptionFileNameInvalid(String str) {
		super("附件名称:{},不合规.", str);
	}
}
