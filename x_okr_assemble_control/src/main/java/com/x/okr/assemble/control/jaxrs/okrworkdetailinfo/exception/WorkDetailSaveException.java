package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkDetailSaveException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkDetailSaveException( Throwable e ) {
		super("系统在保存工作详细信息时发生异常.", e );
	}
}
