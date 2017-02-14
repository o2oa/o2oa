package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfoForIndex;
import com.x.bbs.entity.BBSForumInfo;

@Wrap( BBSForumInfo.class)
public class WrapOutForumInfoForIndex extends BBSForumInfo{
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>();
	
	//论坛版块列表
	private List<WrapOutSectionInfoForIndex> sectionInfoList = null;

	public List<WrapOutSectionInfoForIndex> getSectionInfoList() {
		return sectionInfoList;
	}

	public void setSectionInfoList(List<WrapOutSectionInfoForIndex> sectionInfoList) {
		this.sectionInfoList = sectionInfoList;
	}	
}
