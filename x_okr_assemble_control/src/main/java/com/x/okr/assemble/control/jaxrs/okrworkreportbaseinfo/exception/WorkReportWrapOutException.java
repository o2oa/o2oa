package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkReportWrapOutException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkReportWrapOutException( Throwable e ) {
		super("将查询结果转换为可输出的数据信息时发生异常。", e );
	}
}
