package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class CategoryInfoWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CategoryInfoWrapOutException( Throwable e ) {
		super("将查询出来的分类信息对象转换为可输出的数据信息时发生异常。", e );
	}
}
