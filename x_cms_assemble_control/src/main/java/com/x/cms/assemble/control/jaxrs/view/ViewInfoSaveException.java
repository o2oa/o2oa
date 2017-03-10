package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.exception.PromptException;

class ViewInfoSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ViewInfoSaveException( Throwable e ) {
		super("系统保存视图信息对象时发生异常。", e );
	}
}
