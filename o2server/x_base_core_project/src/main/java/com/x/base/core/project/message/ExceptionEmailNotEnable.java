package com.x.base.core.project.message;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmailNotEnable extends PromptException {

	private static final long serialVersionUID = 6226334698900029283L;

	ExceptionEmailNotEnable() {
		super("email disabled.");
	}
}