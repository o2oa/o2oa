package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class SectionTypeCatagoryInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	SectionTypeCatagoryInvalidException( String type ) {
		super("系统传入的版块[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
	
	SectionTypeCatagoryInvalidException( String[] type ) {
		super("系统传入的版块[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
	
}
