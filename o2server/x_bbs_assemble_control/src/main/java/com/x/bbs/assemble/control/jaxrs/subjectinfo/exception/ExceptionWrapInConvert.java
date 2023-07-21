package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.PromptException;

public class ExceptionWrapInConvert extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWrapInConvert( Throwable e, JsonElement jsonElement) {
		super( "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString(), e);
	}
}
