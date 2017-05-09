package com.x.cms.assemble.control.jaxrs.appdict.exception;

import com.x.base.core.exception.PromptException;

public class AppDictListByAppIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public AppDictListByAppIdException( Throwable e, String appId ) {
		super("根据应用ID查询该应用所有的应用数据字典配置信息时发生异常。AppId：" + appId, e );
	}
}
