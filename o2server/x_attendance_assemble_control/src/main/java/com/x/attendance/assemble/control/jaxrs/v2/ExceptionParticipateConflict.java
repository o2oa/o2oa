package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

import java.util.List;

public class ExceptionParticipateConflict extends PromptException {


	private static final long serialVersionUID = 3096847989690594565L;

	public ExceptionParticipateConflict(List<String> peopleList) {
		super("人员 ：【"+String.join(", ", peopleList) + "】已存在于其它考勤组！");
	}
}
