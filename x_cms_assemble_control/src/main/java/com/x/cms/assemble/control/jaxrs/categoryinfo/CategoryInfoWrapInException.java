package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为一个分类信息对象时发生异常。", e );
	}
}
