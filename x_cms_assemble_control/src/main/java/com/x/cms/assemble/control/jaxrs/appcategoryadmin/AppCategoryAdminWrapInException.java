package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为应用栏目分类管理员信息对象时发生异常。", e );
	}
}
