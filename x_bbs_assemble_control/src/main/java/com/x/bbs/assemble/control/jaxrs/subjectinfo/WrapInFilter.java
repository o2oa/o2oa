package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;

public class WrapInFilter{
	
	private Boolean getBBSTopSubject = true;
	private Boolean getForumTopSubject = true;
	private Boolean getSectionTopSubject = true;
	
	private String forumId = null;
	private String mainSectionId = null;
	private String sectionId = null;
	private String searchContent = null;
	private String creatorName = null;
	private Boolean needPicture = false;
	private Boolean withTopSubject = null; // 是否包含置顶贴
	
	public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodifies );

	public Boolean getGetBBSTopSubject() {
		return getBBSTopSubject;
	}
	public void setGetBBSTopSubject(Boolean getBBSTopSubject) {
		this.getBBSTopSubject = getBBSTopSubject;
	}
	public Boolean getGetForumTopSubject() {
		return getForumTopSubject;
	}
	public void setGetForumTopSubject(Boolean getForumTopSubject) {
		this.getForumTopSubject = getForumTopSubject;
	}
	public Boolean getGetSectionTopSubject() {
		return getSectionTopSubject;
	}
	public void setGetSectionTopSubject(Boolean getSectionTopSubject) {
		this.getSectionTopSubject = getSectionTopSubject;
	}
	public String getForumId() {
		return forumId;
	}
	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getMainSectionId() {
		return mainSectionId;
	}
	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}
	public Boolean getNeedPicture() {
		return needPicture;
	}
	public void setNeedPicture(Boolean needPicture) {
		this.needPicture = needPicture;
	}
	public Boolean getWithTopSubject() {
		return withTopSubject;
	}
	public void setWithTopSubject(Boolean withTopSubject) {
		this.withTopSubject = withTopSubject;
	}
	public String getSearchContent() {
		return searchContent;
	}
	public void setSearchContent( String searchContent ) {
		this.searchContent = searchContent;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
}