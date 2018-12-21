package com.x.processplatform.service.processing.configurator;

public class ManualProcessingConfigurator extends ActivityProcessingConfigurator {

	public Boolean getCallBeforeArrivedExecuteScript() {
		return true;
	}

	public Boolean getCallAfterArrivedExecuteScript() {
		return true;
	}

}