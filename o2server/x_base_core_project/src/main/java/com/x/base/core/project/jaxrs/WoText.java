package com.x.base.core.project.jaxrs;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.HttpMediaType;

public class WoText extends WoMaxAgeFastETag {

	public WoText() {
	}

	public WoText(String text) throws Exception {
		this.text = text;
	}

	@FieldDescribe("text")
	private String text;

	@FieldDescribe("返回Content_Type")
	private String contentType;

	public String getContentType() {
		return StringUtils.isEmpty(this.contentType) ? HttpMediaType.TEXT_PLAIN_UTF_8 : this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
