package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionGetTopUnitNameByPerson extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionGetTopUnitNameByPerson( Throwable e, String name ) {
		super("根据个人信息查询所属顶层组织名称时发生异常！人员:" + name, e );
	}
}
