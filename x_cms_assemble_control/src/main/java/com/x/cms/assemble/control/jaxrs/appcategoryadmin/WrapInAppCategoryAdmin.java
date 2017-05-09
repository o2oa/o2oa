package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.AppCategoryAdmin;

@Wrap(AppCategoryAdmin.class)
public class WrapInAppCategoryAdmin extends AppCategoryAdmin {
	private static final long serialVersionUID = -5076990764713538973L;
	public static final List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodifies);
	private String identity = null;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}
}