package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Debug extends GsonPropertyObject {

	private Boolean x_processplatform_service_processing = false;

	public static Debug defaultInstance() {
		return new Debug();
	}

	public Boolean x_processplatform_service_processing() {
		return BooleanUtils.isTrue(this.x_processplatform_service_processing) ? true : false;
	}

}