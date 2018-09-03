package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.processplatform.core.entity.element.wrap.WrapForm;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;

public class WrapOutApplicationSummary extends WrapOutApplication {

	private static final long serialVersionUID = 1911492899750688757L;
	private List<WrapProcess> processList = new ArrayList<>();
	private List<WrapForm> formList = new ArrayList<>();
	public List<WrapProcess> getProcessList() {
		return processList;
	}
	public void setProcessList(List<WrapProcess> processList) {
		this.processList = processList;
	}
	public List<WrapForm> getFormList() {
		return formList;
	}
	public void setFormList(List<WrapForm> formList) {
		this.formList = formList;
	}

 

}
