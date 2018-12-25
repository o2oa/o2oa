package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionLeaderOpinionEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionLeaderOpinionEmpty() {
		super("领导审批意见为空，无法继续保存汇报信息。" );
	}
}
