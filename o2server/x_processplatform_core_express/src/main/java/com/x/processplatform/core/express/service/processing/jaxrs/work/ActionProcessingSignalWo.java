package com.x.processplatform.core.express.service.processing.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.log.SignalStack;

public class ActionProcessingSignalWo extends WoId {

	private static final long serialVersionUID = 8851351880636501257L;

	@FieldDescribe("流程信号栈.")
	private SignalStack signalStack;

	public SignalStack getSignalStack() {
		if (null == this.signalStack) {
			this.signalStack = new SignalStack();
		}
		return signalStack;
	}

	public void setSignalStack(SignalStack signalStack) {
		this.signalStack = signalStack;
	}

}