package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import com.x.base.core.project.exception.PromptException;

class ExceptionQueryViewNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionQueryViewNotExists( String flag ) {
		super("数据视图信息不存在，无法继续进行操作。id:" + flag );
	}
}
