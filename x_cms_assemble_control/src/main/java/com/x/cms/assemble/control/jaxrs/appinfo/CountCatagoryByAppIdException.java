package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class CountCategoryByAppIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CountCategoryByAppIdException( Throwable e, String id ) {
		super("系统在根据应用栏目ID查询应用下分类个数时发生异常。ID:" + id );
	}
}
