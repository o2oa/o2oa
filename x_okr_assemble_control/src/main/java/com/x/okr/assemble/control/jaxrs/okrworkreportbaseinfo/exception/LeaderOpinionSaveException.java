package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class LeaderOpinionSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public LeaderOpinionSaveException( Throwable e, String id ) {
		super("系统为工作汇报保存领导审批意见时发生异常。ID:" + id, e );
	}
}
