package com.x.meeting.assemble.control.wrapin;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapInOpenMeeting extends GsonPropertyObject {
	private String name;
	private Long numberOfPartizipants;
	private String type;
	private String comment;
	private Boolean isPublic;
	private Boolean allowUserQuestions;
	private Boolean allowRecording;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNumberOfPartizipants() {
		return numberOfPartizipants;
	}

	public void setNumberOfPartizipants(Long numberOfPartizipants) {
		this.numberOfPartizipants = numberOfPartizipants;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Boolean getAllowUserQuestions() {
		return allowUserQuestions;
	}

	public void setAllowUserQuestions(Boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}

	public Boolean getAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(Boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

}
