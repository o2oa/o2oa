package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumTypeCategoryInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumTypeCategoryInvalidException( String type ) {
		super("系统传入的[主题分类]不合法，无法进行数据保存！分类：" + type );
	}
}
