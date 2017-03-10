package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.exception.PromptException;

class AppDictDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppDictDeleteException( Throwable e, String id ) {
		super("应用数据字典配置信息删除时发生异常。ID:" + id, e );
	}
}
