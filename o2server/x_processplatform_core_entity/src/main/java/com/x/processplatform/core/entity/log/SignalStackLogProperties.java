package com.x.processplatform.core.entity.log;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class SignalStackLogProperties extends JsonProperties {

	private static final long serialVersionUID = 5194057760551594662L;
	@FieldDescribe("信号栈.")
	private SignalStack signalStack;

	public SignalStack getSignalStack() {
		if (this.signalStack == null) {
			this.signalStack = new SignalStack();
		}
		return signalStack;
	}

	public void setSignalStack(SignalStack signalStack) {
		this.signalStack = signalStack;
	}

}
