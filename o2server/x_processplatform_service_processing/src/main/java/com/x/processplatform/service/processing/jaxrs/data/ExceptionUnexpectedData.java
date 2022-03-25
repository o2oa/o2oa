package com.x.processplatform.service.processing.jaxrs.data;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.PromptException;

class ExceptionUnexpectedData extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionUnexpectedData(String work, String[] paths, JsonElement jsonElement) {
		super("unexpected post data with work :{}, path :{}, json :{}.", work, StringUtils.join(paths, "."),
				Objects.toString(jsonElement, ""));
	}
}
