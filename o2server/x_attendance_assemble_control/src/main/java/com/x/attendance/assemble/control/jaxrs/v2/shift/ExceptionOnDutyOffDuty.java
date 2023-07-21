package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.x.base.core.project.exception.PromptException;

public class ExceptionOnDutyOffDuty extends PromptException {


	private static final long serialVersionUID = 4017600170180058950L;

	public ExceptionOnDutyOffDuty(String message) {
		super( message );
	}
}
