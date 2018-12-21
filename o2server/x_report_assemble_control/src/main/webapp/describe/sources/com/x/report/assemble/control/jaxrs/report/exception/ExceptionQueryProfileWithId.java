package com.x.report.assemble.control.jaxrs.report.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryProfileWithId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryProfileWithId( Throwable e, String id ) {
		super("系统根据汇报ID查询汇报概要文件 信息时发生异常.profileId:" + id , e );
	}
}
