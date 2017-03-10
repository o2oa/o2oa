package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.exception.PromptException;

class SercretaryConfigSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SercretaryConfigSaveException( Throwable e ) {
		super("保存领导秘书配置信息时发生异常。", e );
	}
}
