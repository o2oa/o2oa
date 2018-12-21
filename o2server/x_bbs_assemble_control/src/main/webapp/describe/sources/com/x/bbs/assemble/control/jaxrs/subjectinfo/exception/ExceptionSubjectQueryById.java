package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectQueryById( Throwable e, String id ) {
		super("根据指定ID查询主题信息时发生异常.ID:" + id, e );
	}
}
