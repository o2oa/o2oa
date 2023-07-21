package com.x.processplatform.service.processing.configurator;

public class EndProcessingConfigurator extends ActivityProcessingConfigurator {

	@Override
	public Boolean getCreateFromWorkLog() {
		return true;
	}

	@Override
	public Boolean getUpdateData() {
		return true;
	}

	@Override
	public Boolean getCalculateExpire() {
		return false;
	}

}