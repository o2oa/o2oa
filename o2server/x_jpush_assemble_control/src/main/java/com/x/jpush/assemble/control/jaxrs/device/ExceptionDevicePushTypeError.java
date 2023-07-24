package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDevicePushTypeError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDevicePushTypeError() {
		super("推送类型不正确，无法进行操作.");
	}
}
