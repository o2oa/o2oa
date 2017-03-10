package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoListByAppIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoListByAppIdException( Throwable e, String id ) {
		super("根据应用栏目ID查询分类信息对象时发生异常。AppId:" + id, e );
	}
}
