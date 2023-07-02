package com.x.cms.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.cms.core.entity.element.wrap.WrapCategoryInfo;
import com.x.cms.core.entity.element.wrap.WrapForm;

public class WrapOutAppInfoSummary extends WrapOutAppInfo {

	private static final long serialVersionUID = 1911492899750688757L;
	private List<WrapCategoryInfo> categoryInfoList = new ArrayList<>();
	private List<WrapForm> formList = new ArrayList<>();
	public List<WrapCategoryInfo> getCategoryInfoList() {
		return categoryInfoList;
	}
	public List<WrapForm> getFormList() {
		return formList;
	}
	public void setCategoryInfoList(List<WrapCategoryInfo> categoryInfoList) {
		this.categoryInfoList = categoryInfoList;
	}
	public void setFormList(List<WrapForm> formList) {
		this.formList = formList;
	}
	
	
}
