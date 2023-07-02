package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeviceParameterEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDeviceParameterEmpty() {
		super("设备号或设备类型为空，无法进行操作.");
	}
}
