package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.exception.PromptException;

public class ExceptionOverAppealMaxTimes extends PromptException {


	private static final long serialVersionUID = -4426607453693973422L;

	public ExceptionOverAppealMaxTimes() {
		super(  "已超过本月最多申诉次数.");
	}
}
