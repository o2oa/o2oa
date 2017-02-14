package com.x.processplatform.assemble.designer.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.processplatform.core.entity.element.Application;

@Wrap(Application.class)
public class WrapOutApplicationSummary extends WrapOutApplication {

	private static final long serialVersionUID = 1911492899750688757L;
	private List<WrapOutProcess> processList = new ArrayList<>();
	private List<WrapOutForm> formList = new ArrayList<>();

	public List<WrapOutProcess> getProcessList() {
		return processList;
	}

	public List<WrapOutForm> getFormList() {
		return formList;
	}

	public void setFormList(List<WrapOutForm> formList) {
		this.formList = formList;
	}

	public void setProcessList(List<WrapOutProcess> processList) {
		this.processList = processList;
	}

}
