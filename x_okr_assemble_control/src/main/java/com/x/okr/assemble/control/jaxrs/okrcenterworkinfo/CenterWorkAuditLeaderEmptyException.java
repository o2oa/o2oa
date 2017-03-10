package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.exception.PromptException;

class CenterWorkAuditLeaderEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CenterWorkAuditLeaderEmptyException() {
		super("中心工作需要审核，但是审核领导信息为空，请检查输入的信息内容。");
	}
}
