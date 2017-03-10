package com.x.cms.assemble.control.jaxrs.appdict;

import com.x.base.core.exception.PromptException;

class AppDictSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AppDictSaveException( Throwable e ) {
		super("应用数据字典配置信息保存时发生异常。", e );
	}
}
