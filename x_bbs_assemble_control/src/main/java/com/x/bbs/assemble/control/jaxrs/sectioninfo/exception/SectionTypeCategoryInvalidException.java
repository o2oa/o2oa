package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class SectionTypeCategoryInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SectionTypeCategoryInvalidException( String type ) {
		super("系统传入的版块[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
}
