package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoListByApplicationException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoListByApplicationException( Throwable e, String application ) {
		super("根据应用名称查询信息对象列表时发生异常。Application:" + application, e );
	}
	
	InfoListByApplicationException( Throwable e, String application, String id ) {
		super("根据应用名称查询信息对象列表时发生异常。Application:" + application + ", InfoId:" + id , e );
	}
}
