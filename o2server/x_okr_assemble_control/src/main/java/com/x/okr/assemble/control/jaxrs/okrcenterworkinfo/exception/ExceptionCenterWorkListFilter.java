package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCenterWorkListFilter extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCenterWorkListFilter( Throwable e, String id, Integer count) {
		super("分页搜索中心工作信息时发生异常。Id:" + id + ", Count:" + count, e);
	}
}
