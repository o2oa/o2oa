package com.x.attendance.assemble.control.jaxrs.workplace;

import com.x.base.core.project.exception.PromptException;

public class ExceptionLatitudeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionLatitudeEmpty() {
		super("工作场所坐标纬度不允许为空，无法进行数据保存。");
	}
}
