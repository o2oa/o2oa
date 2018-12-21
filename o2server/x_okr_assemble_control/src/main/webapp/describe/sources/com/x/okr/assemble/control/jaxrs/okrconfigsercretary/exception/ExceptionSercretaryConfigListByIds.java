package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSercretaryConfigListByIds extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSercretaryConfigListByIds( Throwable e ) {
		super("系统根据ID列表查询领导秘书配置信息时发生异常。", e);
	}
}
