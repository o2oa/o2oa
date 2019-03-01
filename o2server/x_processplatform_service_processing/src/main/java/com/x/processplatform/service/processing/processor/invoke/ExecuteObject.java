package com.x.processplatform.service.processing.processor.invoke;

import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.service.processing.processor.AeiObjects;

public class ExecuteObject {

	public ExecuteObject(AeiObjects aeiObjects, Invoke invoke) {
		this.aeiObjects = aeiObjects;
		this.invoke = invoke;
	}

	private AeiObjects aeiObjects;

	private Invoke invoke;

	public AeiObjects getAeiObjects() {
		return aeiObjects;
	}

	public void setAeiObjects(AeiObjects aeiObjects) {
		this.aeiObjects = aeiObjects;
	}

	public Invoke getInvoke() {
		return invoke;
	}

	public void setInvoke(Invoke invoke) {
		this.invoke = invoke;
	}

}
