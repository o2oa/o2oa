package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SubjectContentQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectContentQueryByIdException( Throwable e, String id ) {
		super("系统在根据ID查询主题的内容时发生异常.ID:" + id, e );
	}
}
