package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCenterWorkIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCenterWorkIdEmpty() {
		super("中心工作id为空，无法进行保存或者查询。" );
	}
}
