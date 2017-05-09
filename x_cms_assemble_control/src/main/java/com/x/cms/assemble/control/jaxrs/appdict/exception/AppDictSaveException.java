package com.x.cms.assemble.control.jaxrs.appdict.exception;

import com.x.base.core.exception.PromptException;

public class AppDictSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppDictSaveException( Throwable e ) {
		super("应用数据字典配置信息保存时发生异常。", e );
	}
}
