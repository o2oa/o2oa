package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.exception.PromptException;

class AttendanceWorkPlaceWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AttendanceWorkPlaceWrapOutException( Throwable e ) {
		super("系统将查询结果转换为可输出的数据信息时发生异常。", e );
	}
}
