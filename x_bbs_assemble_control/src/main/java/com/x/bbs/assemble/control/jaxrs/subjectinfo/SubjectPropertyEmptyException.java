package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SubjectPropertyEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SubjectPropertyEmptyException( String name ) {
		super("传入的数据不完整，无法保存主题信息.属性:" + name );
	}
}
