package com.x.crm.assemble.control.jaxrs.crmbaseconfig;

import com.x.base.core.project.exception.PromptException;

public class RegionSuperiorPidException extends PromptException {

	private static final long serialVersionUID = 3569527529288359612L;

	RegionSuperiorPidException() {
		super("查询失败,上一行政组织所需的参数Pid错误: 无Pid参数,或者pid为空.");
	}

}
