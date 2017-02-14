package com.x.processplatform.service.processing.configurator;

public abstract class ActivityProcessingConfigurator {

	private Boolean callBeforeArriveScript = true;
	private Boolean callAfterArriveScript = true;
	private Boolean callBeforeExecuteScript = true;
	private Boolean callAfterExecuteScript = true;
	private Boolean callBeforeInquireScript = true;
	private Boolean callAfterInquireScript = true;
	private Boolean callBeforeArrivedExecuteScript = false;
	private Boolean callAfterArrivedExecuteScript = false;
	private Boolean createRead = true;
	private Boolean createReview = true;
	private Boolean stampArrivedWorkLog = true;
	private Boolean createFromWorkLog = true;
	private Boolean changeActivityToken = true;
	private Boolean updateData = true;
	private Boolean calculateExpire = true;

	public Boolean getCallBeforeArriveScript() {
		return callBeforeArriveScript;
	}

	public Boolean getCallAfterArriveScript() {
		return callAfterArriveScript;
	}

	public Boolean getCallBeforeExecuteScript() {
		return callBeforeExecuteScript;
	}

	public Boolean getCallAfterExecuteScript() {
		return callAfterExecuteScript;
	}

	public Boolean getCallBeforeInquireScript() {
		return callBeforeInquireScript;
	}

	public Boolean getCallAfterInquireScript() {
		return callAfterInquireScript;
	}

	public Boolean getCreateRead() {
		return createRead;
	}

	public Boolean getCreateReview() {
		return createReview;
	}

	public Boolean getStampArrivedWorkLog() {
		return stampArrivedWorkLog;
	}

	public Boolean getCreateFromWorkLog() {
		return createFromWorkLog;
	}

	public Boolean getChangeActivityToken() {
		return changeActivityToken;
	}

	public Boolean getUpdateData() {
		return updateData;
	}

	public Boolean getCalculateExpire() {
		return calculateExpire;
	}

	public Boolean getCallBeforeArrivedExecuteScript() {
		return callBeforeArrivedExecuteScript;
	}

	public Boolean getCallAfterArrivedExecuteScript() {
		return callAfterArrivedExecuteScript;
	}

}
