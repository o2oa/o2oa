package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrConfigSecretary;

@Wrap( OkrConfigSecretary.class)
public class WrapInOkrConfigSecretary extends OkrConfigSecretary {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	private String userName = null;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}