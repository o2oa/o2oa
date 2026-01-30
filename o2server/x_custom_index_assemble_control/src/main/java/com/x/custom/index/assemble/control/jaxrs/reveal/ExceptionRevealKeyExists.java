package com.x.custom.index.assemble.control.jaxrs.reveal;

import java.util.List;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.gson.XGsonBuilder;

class ExceptionRevealKeyExists extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionRevealKeyExists(List<String> processPlatformKeyList, List<String> cmsKeyList) {
		super("reveal has keys already exists, processPlatform:{}, cms:{}.",
				XGsonBuilder.toJson(processPlatformKeyList), XGsonBuilder.toJson(cmsKeyList));
	}
}
