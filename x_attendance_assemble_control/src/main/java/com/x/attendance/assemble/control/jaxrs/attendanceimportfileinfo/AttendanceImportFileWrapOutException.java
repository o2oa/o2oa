package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AttendanceImportFileWrapOutException( Throwable e ) {
		super("将所有查询出来的有状态的导入文件对象转换为可以输出的过滤过属性的对象时发生异常.", e );
	}
}
