package com.x.attendance.assemble.control.jaxrs.v2.appeal;

import com.x.base.core.project.exception.PromptException;

public class ExceptionAppealNotEnable extends PromptException {


	private static final long serialVersionUID = 4437932988958821670L;

	public ExceptionAppealNotEnable() {
		super(  "未启用申诉功能.");
	}
}
