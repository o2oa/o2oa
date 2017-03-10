package com.x.cms.assemble.control.jaxrs.appdictitem;

import com.x.base.core.exception.PromptException;

class AppDictItemDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppDictItemDeleteException( Throwable e, String appDictId, String ...path ) {
		super("应用数据字典属性配置信息删除时发生异常。AppDictId:" + appDictId + ", path:" + path, e );
	}
}
