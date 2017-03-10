package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import com.x.base.core.exception.PromptException;

class WorkLevelConfigSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkLevelConfigSaveException( Throwable e ) {
		super("系统保存工作级别配置时发生异常。", e);
	}
}
