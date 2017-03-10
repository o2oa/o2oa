package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import com.x.base.core.exception.PromptException;

class WorkLevelConfigDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkLevelConfigDeleteException( Throwable e, String id ) {
		super("系统根据ID查询指定的工作级别配置时发生异常。ID:" + id, e);
	}
}
