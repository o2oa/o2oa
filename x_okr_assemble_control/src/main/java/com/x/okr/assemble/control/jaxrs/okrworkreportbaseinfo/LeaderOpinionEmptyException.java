package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.exception.PromptException;

class LeaderOpinionEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	LeaderOpinionEmptyException() {
		super("领导审批意见为空，无法继续保存汇报信息。" );
	}
}
