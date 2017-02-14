package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.bbs.entity.BBSSubjectInfo;

@Wrap( BBSSubjectInfo.class)
public class WrapOutSubjectInfo extends BBSSubjectInfo{
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();
	
	private List<WrapOutSubjectAttachment> subjectAttachmentList;
	
	@EntityFieldDescribe( "投票主题的所有投票选项列表." )
	private List<WrapOutBBSVoteOption> voteOptionList;
	
	private String content = null;
	
	private String pictureBase64 = null;
	
	private String voteResult = null;

	public List<WrapOutSubjectAttachment> getSubjectAttachmentList() {
		return subjectAttachmentList;
	}

	public void setSubjectAttachmentList(List<WrapOutSubjectAttachment> subjectAttachmentList) {
		this.subjectAttachmentList = subjectAttachmentList;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPictureBase64() {
		return pictureBase64;
	}

	public void setPictureBase64(String pictureBase64) {
		this.pictureBase64 = pictureBase64;
	}

	public String getVoteResult() {
		return voteResult;
	}

	public void setVoteResult(String voteResult) {
		this.voteResult = voteResult;
	}

	public List<WrapOutBBSVoteOption> getVoteOptionList() {
		return voteOptionList;
	}

	public void setVoteOptionList(List<WrapOutBBSVoteOption> voteOptionList) {
		this.voteOptionList = voteOptionList;
	}
	
}
