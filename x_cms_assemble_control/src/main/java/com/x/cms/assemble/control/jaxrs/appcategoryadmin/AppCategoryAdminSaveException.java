package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminSaveException( Throwable e ) {
		super("应用栏目分类管理员配置信息保存时发生异常。", e );
	}
}
