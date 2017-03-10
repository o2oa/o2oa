package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminListAllException( Throwable e ) {
		super("查询所有应用栏目分类管理员配置信息时发生异常。", e );
	}
}
