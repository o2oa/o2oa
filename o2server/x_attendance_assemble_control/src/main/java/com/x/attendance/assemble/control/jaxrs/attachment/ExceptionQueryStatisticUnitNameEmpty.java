package com.x.attendance.assemble.control.jaxrs.attachment;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryStatisticUnitNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryStatisticUnitNameEmpty() {
		super("系统未获取到查询参数组织名称name，无法进行数据查询");
	}
}
