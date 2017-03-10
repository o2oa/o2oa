package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.x.base.core.exception.PromptException;

class SerialNumberNotExistedException extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	SerialNumberNotExistedException(String flag) {
		super("serialNumber: {} not existed.", flag);
	}
}
