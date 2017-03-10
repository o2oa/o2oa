package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminDeleteException( Throwable e, String id ) {
		super("根据ID删除应用栏目分类管理员配置信息时发生异常。ID:" + id, e );
	}
}
