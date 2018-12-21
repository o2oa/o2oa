package com.x.cms.assemble.control.jaxrs.queryview.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionQueryViewNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionQueryViewNotExists( String flag ) {
		super("数据视图信息不存在，无法继续进行操作。id:" + flag );
	}
}
