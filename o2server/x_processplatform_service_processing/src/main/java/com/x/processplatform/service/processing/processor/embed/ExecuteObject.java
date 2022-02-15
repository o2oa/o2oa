package com.x.processplatform.service.processing.processor.embed;

import com.x.processplatform.core.entity.element.Embed;

public class ExecuteObject {

	public ExecuteObject(AssignData assignData, Embed embed) {
		this.assignData = assignData;
		this.embed = embed;
	}

	private AssignData assignData;

	private Embed embed;

	public AssignData getAssignData() {
		return assignData;
	}

	public void setAssignData(AssignData assignData) {
		this.assignData = assignData;
	}

	public Embed getEmbed() {
		return embed;
	}

	public void setEmbed(Embed embed) {
		this.embed = embed;
	}

}
