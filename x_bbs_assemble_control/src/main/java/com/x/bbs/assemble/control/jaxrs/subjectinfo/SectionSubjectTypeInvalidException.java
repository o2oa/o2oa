package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SectionSubjectTypeInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionSubjectTypeInvalidException( String type ) {
		super("系统传入的版块[信息类别]不合法，无法进行数据保存！分类：" + type );
	}
	
	SectionSubjectTypeInvalidException( String[] type ) {
		super("系统传入的版块[信息类别]不合法，无法进行数据保存！分类：" + type );
	}
}
