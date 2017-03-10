package com.x.okr.assemble.control.jaxrs.mind;

import com.x.base.core.exception.PromptException;

class ViewableWorkListException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ViewableWorkListException( Throwable e, String name, String centerId ) {
		super("获取用户可以看到的所有具体工作信息发生异常!Name:" + name + ", CenterId:" + centerId );
	}
	
	ViewableWorkListException( Throwable e, String name ) {
		super("获取用户可以看到的所有具体工作信息发生异常!Name:" + name );
	}
}
