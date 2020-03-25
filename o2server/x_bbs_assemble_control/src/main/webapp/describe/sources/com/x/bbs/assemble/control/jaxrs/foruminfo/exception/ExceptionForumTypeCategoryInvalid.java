package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumTypeCategoryInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumTypeCategoryInvalid( String type ) {
		super("系统传入的[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
}
