package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminQueryByIdException( Throwable e, String id ) {
		super("根据ID查询应用栏目分类管理员配置信息时发生异常。ID:" + id, e );
	}
}
