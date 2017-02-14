package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;

public class WrapInFilter{

	private String subjectId = null;
	
	public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodifies );

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
}