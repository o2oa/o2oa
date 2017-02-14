package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfo;
import com.x.bbs.entity.BBSPermissionInfo;

@Wrap( BBSPermissionInfo.class)
public class WrapOutPermissionInfo extends BBSPermissionInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
	
	//论坛版块列表
	private List<WrapOutSectionInfo> sections = null;

	public List<WrapOutSectionInfo> getSections() {
		return sections;
	}

	public void setSections(List<WrapOutSectionInfo> sections) {
		this.sections = sections;
	}
	
	
}
