package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.exception.PromptException;

class AttendanceWorkPlaceListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkPlaceListAllException( Throwable e ) {
		super("系统在查询所有的工作场所信息对象时发生异常。", e );
	}
}
