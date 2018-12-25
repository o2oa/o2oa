package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoWrapOut extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoWrapOut( Throwable e ) {
		super("将查询结果转换为可以输出的数据信息时发生异常.", e );
	}
}
