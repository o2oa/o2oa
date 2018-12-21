package com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSercretaryConfigQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSercretaryConfigQueryById( Throwable e, String id ) {
		super("系统根据ID查询领导秘书配置信息时发生异常。ID:"+id, e);
	}
}
