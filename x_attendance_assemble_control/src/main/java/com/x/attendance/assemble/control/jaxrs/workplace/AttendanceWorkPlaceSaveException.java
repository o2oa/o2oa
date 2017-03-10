package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.exception.PromptException;

class AttendanceWorkPlaceSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkPlaceSaveException( Throwable e) {
		super("工作场所名称不允许为空，无法进行数据保存。", e);
	}
}
