package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import com.x.base.core.project.exception.PromptException;

class ExceptionCanNotFindTopUnitNameByPerson extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCanNotFindTopUnitNameByPerson( String name ) {
		super("根据个人信息查询未能查询到所属顶层组织名称，请检查人员所属组织配置！人员:" + name );
	}
}
