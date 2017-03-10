package com.x.okr.assemble.control.jaxrs.mind;

import com.x.base.core.exception.PromptException;

class DeployedWorkListAllException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DeployedWorkListAllException( Throwable e, String centerId ) {
		super("获取所有已经部署的具体工作信息发生异常!CenterId:" + centerId );
	}
}
