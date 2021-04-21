package com.x.cms.assemble.control.jaxrs.queryview;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionQueryViewNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionQueryViewNotExists( String flag ) {
		super("数据视图信息不存在，无法继续进行操作。id:{}", flag );
	}
}
