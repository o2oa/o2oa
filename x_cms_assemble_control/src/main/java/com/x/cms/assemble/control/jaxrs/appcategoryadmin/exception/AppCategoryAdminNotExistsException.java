package com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception;

import com.x.base.core.exception.PromptException;

public class AppCategoryAdminNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppCategoryAdminNotExistsException( String id ) {
		super("指定ID的应用栏目分类管理员配置信息不存在。ID:" + id );
	}
}
