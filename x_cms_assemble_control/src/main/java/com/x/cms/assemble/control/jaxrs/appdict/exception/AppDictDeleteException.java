package com.x.cms.assemble.control.jaxrs.appdict.exception;

import com.x.base.core.exception.PromptException;

public class AppDictDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppDictDeleteException( Throwable e, String id ) {
		super("应用数据字典配置信息删除时发生异常。ID:" + id, e );
	}
}
