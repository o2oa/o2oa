package com.x.cms.assemble.control.jaxrs.catagoryinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.CatagoryInfo;

@Wrap(CatagoryInfo.class)
public class WrapInCatagoryInfo extends CatagoryInfo {
	private static final long serialVersionUID = -5076990764713538973L;
	public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	
	private String identity = null;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
}