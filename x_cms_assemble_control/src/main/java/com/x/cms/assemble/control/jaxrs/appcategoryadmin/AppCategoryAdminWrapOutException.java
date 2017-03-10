package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminWrapOutException( Throwable e ) {
		super("系统将查询出来的应用栏目分类管理员信息转换为输出格式时发生异常。", e );
	}
}
