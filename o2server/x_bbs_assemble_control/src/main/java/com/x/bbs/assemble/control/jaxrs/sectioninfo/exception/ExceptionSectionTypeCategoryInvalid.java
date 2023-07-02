package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionTypeCategoryInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionTypeCategoryInvalid( String type ) {
		super("系统传入的版块[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
}
