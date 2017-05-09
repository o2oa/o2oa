package com.x.cms.assemble.control.jaxrs.appdict.exception;

import com.x.base.core.exception.PromptException;

public class AppDictUpdateException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppDictUpdateException( Throwable e, String id ) {
		super("根据ID更新指定的应用数据字典配置信息时发生异常。ID：" + id, e );
	}
}
