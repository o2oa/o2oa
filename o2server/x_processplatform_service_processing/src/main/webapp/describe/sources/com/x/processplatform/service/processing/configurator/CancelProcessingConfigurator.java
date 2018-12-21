package com.x.processplatform.service.processing.configurator;

public class CancelProcessingConfigurator extends ActivityProcessingConfigurator {

	@Override
	public Boolean getCreateFromWorkLog() {
		return true;
	}

	@Override
	public Boolean getUpdateData() {
		return false;
	}

	@Override
	public Boolean getCalculateExpire() {
		return false;
	}

}