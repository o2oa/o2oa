package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoCheckEditAvailableException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoCheckEditAvailableException( Throwable e, String id ) {
		super("系统在检查分类信息是否允许被用户编辑时发生异常。ID:" + id );
	}
}
