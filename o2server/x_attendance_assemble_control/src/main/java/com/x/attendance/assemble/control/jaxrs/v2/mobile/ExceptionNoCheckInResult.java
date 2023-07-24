package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.base.core.project.exception.PromptException;

public class ExceptionNoCheckInResult extends PromptException {


	private static final long serialVersionUID = 5464536178018732958L;

	public ExceptionNoCheckInResult() {
		super(  "没有找到符合打卡的信息.");
	}
}
