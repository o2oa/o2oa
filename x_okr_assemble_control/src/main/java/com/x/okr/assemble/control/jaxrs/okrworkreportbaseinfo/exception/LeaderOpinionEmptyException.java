package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class LeaderOpinionEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public LeaderOpinionEmptyException() {
		super("领导审批意见为空，无法继续保存汇报信息。" );
	}
}
