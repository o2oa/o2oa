package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.exception.PromptException;

class ViewInfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ViewInfoWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为一个视图信息对象时发生异常。", e );
	}
}
