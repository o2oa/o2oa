package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.exception.PromptException;

class AttendanceWorkPlaceyQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkPlaceyQueryByIdException( Throwable e, String id ) {
		super("系统根据ID查询工作场所对象信息时发生异常。ID:" + id, e );
	}
}
