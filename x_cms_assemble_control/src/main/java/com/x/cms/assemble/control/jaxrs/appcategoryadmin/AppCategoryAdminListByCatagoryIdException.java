package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminListByCategoryIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminListByCategoryIdException( Throwable e, String id ) {
		super("根据分类ID查询所有对应的应用栏目分类管理员配置信息列表时发生异常。CategoryId:" + id, e );
	}
}
