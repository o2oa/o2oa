package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import com.x.base.core.exception.PromptException;

class WorkTypeConfigSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkTypeConfigSaveException( Throwable e ) {
		super("系统保存工作类别配置时发生异常。", e);
	}
}
