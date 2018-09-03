package com.x.crm.assemble.control.jaxrs.crmbaseconfig;

import com.x.base.core.project.exception.PromptException;

public class RegionNameNotExistException extends PromptException {

	private static final long serialVersionUID = 3569527529288359612L;

	RegionNameNotExistException() {
		super("查询失败,行政组织名称错误: 无此名称的省，或者城市.");
	}

}
