package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class EffectivePersonGetException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public EffectivePersonGetException(Throwable e ) {
		super("系统在获取登录用户信息时发生异常.", e);
	}
}
