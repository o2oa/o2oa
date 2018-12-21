package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPublicSectionFilter extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPublicSectionFilter( Throwable e ) {
		super("根据指定条件查询所有公开的分区信息时发生异常.", e );
	}
}
