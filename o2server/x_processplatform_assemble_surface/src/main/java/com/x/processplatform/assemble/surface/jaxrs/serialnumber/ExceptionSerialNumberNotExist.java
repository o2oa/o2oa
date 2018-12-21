package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.project.exception.PromptException;

class ExceptionSerialNumberNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionSerialNumberNotExist(String flag) {
		super("serialNumber: {} not existed.", flag);
	}
}
