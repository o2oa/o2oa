package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSSectionInfo;

@Wrap( BBSSectionInfo.class)
public class WrapOutSectionInfo extends BBSSectionInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
	
	//版块的子版块信息列表
	private List<WrapOutSectionInfo> subSections = null;

	public List<WrapOutSectionInfo> getSubSections() {
		return subSections;
	}
	public void setSubSections(List<WrapOutSectionInfo> subSections) {
		this.subSections = subSections;
	}	
}
