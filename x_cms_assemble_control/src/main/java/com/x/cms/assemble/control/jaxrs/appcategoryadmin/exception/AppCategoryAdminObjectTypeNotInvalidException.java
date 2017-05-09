package com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryAdminObjectTypeNotInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryAdminObjectTypeNotInvalidException( String type ) {
		super("应用栏目分类管理员配置信息对象类别不合法，可选值为：APPINFO|CATEGORY，实际值：" + type );
	}
}
