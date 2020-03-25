package com.x.processplatform.service.processing.processor.embed;

import com.x.processplatform.core.entity.element.Embed;

public class ExecuteObject {

	public ExecuteObject(AssginData assginData, Embed embed) {
		this.assginData = assginData;
		this.embed = embed;
	}

	private AssginData assginData;

	private Embed embed;

	public AssginData getAssginData() {
		return assginData;
	}

	public void setAssginData(AssginData assginData) {
		this.assginData = assginData;
	}

	public Embed getEmbed() {
		return embed;
	}

	public void setEmbed(Embed embed) {
		this.embed = embed;
	}

}
