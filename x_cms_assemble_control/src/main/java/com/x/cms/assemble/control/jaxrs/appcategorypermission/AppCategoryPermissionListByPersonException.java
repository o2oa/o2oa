package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import java.util.List;

import com.x.base.core.exception.PromptException;

class AppCategoryPermissionListByPersonException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppCategoryPermissionListByPersonException( Throwable e, String person ) {
		super("根据用户姓名查询所有对应的应用栏目分类权限配置信息列表时发生异常。Person:" + person, e );
	}
	
	AppCategoryPermissionListByPersonException( Throwable e, String person, String objectType ) {
		super("根据用户姓名和对象类别查询所有对应的应用栏目分类权限配置信息列表时发生异常。Person:" + person+ ", ObjectType:" + objectType , e );
	}
	
	AppCategoryPermissionListByPersonException( Throwable e, String person, List<String> categoryIds ) {
		super("根据用户姓名和分类ID列表查询所有对应的应用栏目分类权限配置信息列表时发生异常。Person:" + person+ ", CategoryIds:" + categoryIds , e );
	}
}
