package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class ReadCompletedProperties extends JsonProperties {

	private static final long serialVersionUID = -2261977487908320276L;

	@FieldDescribe("标题")
	private String title;

	@FieldDescribe("意见")
	private String opinion;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
}
