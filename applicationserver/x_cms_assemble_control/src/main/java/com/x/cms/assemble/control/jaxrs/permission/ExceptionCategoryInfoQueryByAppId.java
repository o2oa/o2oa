package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.PromptException;

class ExceptionCategoryInfoQueryByAppId extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoQueryByAppId( Throwable e, String id ) {
		super( "根据指定栏目ID查询分类信息对象时发生异常。APPID:{}" ,id, e );
	}
}
