package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.exception.PromptException;

public class SercretaryConfigSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SercretaryConfigSaveException( Throwable e ) {
		super("保存领导秘书配置信息时发生异常。", e );
	}
}
