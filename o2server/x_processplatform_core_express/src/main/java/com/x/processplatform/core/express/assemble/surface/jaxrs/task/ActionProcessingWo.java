package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.log.SignalStack;

public class ActionProcessingWo extends Record {

	private static final long serialVersionUID = -8450939016187545724L;

	private SignalStack signalStack;

	private Boolean occurSignalStack;

	public SignalStack getSignalStack() {
		return signalStack;
	}

	public void setSignalStack(SignalStack signalStack) {
		this.signalStack = signalStack;
	}

	public Boolean getOccurSignalStack() {
		return occurSignalStack;
	}

	public void setOccurSignalStack(Boolean occurSignalStack) {
		this.occurSignalStack = occurSignalStack;
	}

}