package com.x.bbs.assemble.control.jaxrs.userinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;

public class WrapInFilter{
	
	private String userName = null;
	
	public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodifies );

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
}