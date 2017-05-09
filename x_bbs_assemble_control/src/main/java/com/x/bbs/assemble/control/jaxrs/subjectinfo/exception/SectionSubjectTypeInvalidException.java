package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionSubjectTypeInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionSubjectTypeInvalidException( String type ) {
		super("系统传入的版块[信息类别]不合法，无法进行数据保存！分类：" + type );
	}
	
	public SectionSubjectTypeInvalidException( String[] type ) {
		super("系统传入的版块[信息类别]不合法，无法进行数据保存！分类：" + type );
	}
}
