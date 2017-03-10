package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import com.x.base.core.exception.PromptException;

class CanNotFindCompanyWithOrganNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CanNotFindCompanyWithOrganNameException( String name ) {
		super("未能根据组织信息查询到所属公司名称.组织：" + name );
	}
}
