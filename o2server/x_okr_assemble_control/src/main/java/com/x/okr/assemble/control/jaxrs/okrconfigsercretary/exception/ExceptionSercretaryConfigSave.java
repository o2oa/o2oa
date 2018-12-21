package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSercretaryConfigSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSercretaryConfigSave( Throwable e ) {
		super("保存领导秘书配置信息时发生异常。", e );
	}
}
