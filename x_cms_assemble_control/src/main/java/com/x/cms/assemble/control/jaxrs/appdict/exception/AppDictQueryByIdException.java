package com.x.cms.assemble.control.jaxrs.appdict.exception;

import com.x.base.core.exception.PromptException;

public class AppDictQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppDictQueryByIdException( Throwable e, String id ) {
		super("根据ID查询指定的应用数据字典配置信息时发生异常。ID：" + id, e );
	}
}
