package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCannotOperateGroup extends PromptException {

 

	private static final long serialVersionUID = 2816307410375246495L;

	public ExceptionCannotOperateGroup(String name) {
		super(name + " 考勤组未保存使用，无法操作.");
	}
}
