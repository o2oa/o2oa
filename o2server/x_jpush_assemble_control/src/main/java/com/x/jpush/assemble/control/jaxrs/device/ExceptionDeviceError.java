package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeviceError extends PromptException {


	private static final long serialVersionUID = -7628244339899708417L;

	public ExceptionDeviceError(String msg) {
		super("错误: {}", msg);
	}
}
