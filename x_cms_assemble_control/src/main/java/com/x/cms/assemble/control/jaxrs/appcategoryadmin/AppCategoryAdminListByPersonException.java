package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.exception.PromptException;

class AppCategoryAdminListByPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryAdminListByPersonException( Throwable e, String person ) {
		super("根据管理员姓名查询所有对应的应用栏目分类管理员配置信息列表时发生异常。Person:" + person, e );
	}
	
	AppCategoryAdminListByPersonException( Throwable e, String person, String objectType ) {
		super("根据管理员姓名和对象类别查询所有对应的应用栏目分类管理员配置信息列表时发生异常。Person:" + person+ ", ObjectType:" + objectType , e );
	}
}
