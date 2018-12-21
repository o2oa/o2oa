package com.x.okr.assemble.control.jaxrs.mind.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeployedWorkListAll extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDeployedWorkListAll( Throwable e, String centerId ) {
		super("获取所有已经部署的具体工作信息发生异常!CenterId:" + centerId );
	}
}
