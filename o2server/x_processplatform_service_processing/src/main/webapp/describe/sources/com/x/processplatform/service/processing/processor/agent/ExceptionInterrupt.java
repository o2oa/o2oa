package com.x.processplatform.service.processing.processor.agent;

import com.x.base.core.project.exception.RunningException;

class ExceptionInterrupt extends RunningException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionInterrupt(Exception e, String title, String workId, String agentName, String agentId, String processName,
			String processId) {
		super(e, "工作:{}, id:{}, 在Agent节点:{}, id:{}, 流程:{}, id:{},运行中断.", title, workId, agentName, agentId, processName,
				processId);
	}

}
