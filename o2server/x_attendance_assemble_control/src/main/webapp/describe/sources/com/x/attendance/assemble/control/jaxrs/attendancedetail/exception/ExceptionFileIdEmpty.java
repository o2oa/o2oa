package com.x.attendance.assemble.control.jaxrs.attendancedetail.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFileIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileIdEmpty() {
		super("员工打卡记录导入文件ID为空，无法进行数据查询." );
	}
}
