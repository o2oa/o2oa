package com.x.processplatform.service.processing.configurator;

public class EndProcessingConfigurator extends ActivityProcessingConfigurator {

	@Override
	public Boolean getUpdateData() {
		return false;
	}

	@Override
	public Boolean getCalculateExpire() {
		return false;
	}

}