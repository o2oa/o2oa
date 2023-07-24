package com.x.processplatform.service.processing.processor.embed;

import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi;

public class ExecuteObject {

	public ExecuteObject(ActionAssignCreateWi assignData, Embed embed) {
		this.assignData = assignData;
		this.embed = embed;
	}

	private ActionAssignCreateWi assignData;

	private Embed embed;

	public ActionAssignCreateWi getAssignData() {
		return assignData;
	}

	public void setAssignData(ActionAssignCreateWi assignData) {
		this.assignData = assignData;
	}

	public Embed getEmbed() {
		return embed;
	}

	public void setEmbed(Embed embed) {
		this.embed = embed;
	}

}
