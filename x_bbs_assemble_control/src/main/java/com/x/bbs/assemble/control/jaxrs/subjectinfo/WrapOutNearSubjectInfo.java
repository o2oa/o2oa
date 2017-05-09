package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapOutNearSubjectInfo.class)
public class WrapOutNearSubjectInfo{

	private WrapOutSubjectInfo lastSubject = null;
	
	private WrapOutSubjectInfo currentSubject = null;
	
	private WrapOutSubjectInfo nextSubject = null;

	public WrapOutSubjectInfo getLastSubject() {
		return lastSubject;
	}

	public void setLastSubject(WrapOutSubjectInfo lastSubject) {
		this.lastSubject = lastSubject;
	}

	public WrapOutSubjectInfo getNextSubject() {
		return nextSubject;
	}

	public void setNextSubject(WrapOutSubjectInfo nextSubject) {
		this.nextSubject = nextSubject;
	}

	public WrapOutSubjectInfo getCurrentSubject() {
		return currentSubject;
	}
	public void setCurrentSubject(WrapOutSubjectInfo currentSubject) {
		this.currentSubject = currentSubject;
	}	
}
