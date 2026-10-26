package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAppealLocked extends PromptException {


	private static final long serialVersionUID = -2802638812469988244L;

	public ExceptionAppealLocked() {
		super(  "当前数据已锁定.");
	}
}
