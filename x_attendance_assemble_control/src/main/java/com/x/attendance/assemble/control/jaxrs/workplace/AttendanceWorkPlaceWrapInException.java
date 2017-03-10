package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.exception.PromptException;

class AttendanceWorkPlaceWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkPlaceWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为一个工作场所对象信息时发生异常。", e );
	}
}
