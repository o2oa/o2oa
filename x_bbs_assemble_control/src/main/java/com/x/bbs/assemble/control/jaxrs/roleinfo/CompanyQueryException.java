package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class CompanyQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	CompanyQueryException( Throwable e, String name ) {
		super("公司信息查询时发生异常！Company:" + name );
	}
}
