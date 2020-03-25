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

	public void setCallBeforeArriveScript(Boolean callBeforeArriveScript) {
		this.callBeforeArriveScript = callBeforeArriveScript;
	}

	public void setCallAfterArriveScript(Boolean callAfterArriveScript) {
		this.callAfterArriveScript = callAfterArriveScript;
	}

	public void setCallBeforeExecuteScript(Boolean callBeforeExecuteScript) {
		this.callBeforeExecuteScript = callBeforeExecuteScript;
	}

	public void setCallAfterExecuteScript(Boolean callAfterExecuteScript) {
		this.callAfterExecuteScript = callAfterExecuteScript;
	}

	public void setCallBeforeInquireScript(Boolean callBeforeInquireScript) {
		this.callBeforeInquireScript = callBeforeInquireScript;
	}

	public void setCallAfterInquireScript(Boolean callAfterInquireScript) {
		this.callAfterInquireScript = callAfterInquireScript;
	}

	public void setCallBeforeArrivedExecuteScript(Boolean callBeforeArrivedExecuteScript) {
		this.callBeforeArrivedExecuteScript = callBeforeArrivedExecuteScript;
	}

	public void setCallAfterArrivedExecuteScript(Boolean callAfterArrivedExecuteScript) {
		this.callAfterArrivedExecuteScript = callAfterArrivedExecuteScript;
	}

	public void setCreateRead(Boolean createRead) {
		this.createRead = createRead;
	}

	public void setCreateReview(Boolean createReview) {
		this.createReview = createReview;
	}

	public void setStampArrivedWorkLog(Boolean stampArrivedWorkLog) {
		this.stampArrivedWorkLog = stampArrivedWorkLog;
	}

	public void setCreateFromWorkLog(Boolean createFromWorkLog) {
		this.createFromWorkLog = createFromWorkLog;
	}

	public void setChangeActivityToken(Boolean changeActivityToken) {
		this.changeActivityToken = changeActivityToken;
	}

	public void setUpdateData(Boolean updateData) {
		this.updateData = updateData;
	}

	public void setCalculateExpire(Boolean calculateExpire) {
		this.calculateExpire = calculateExpire;
	}

}
