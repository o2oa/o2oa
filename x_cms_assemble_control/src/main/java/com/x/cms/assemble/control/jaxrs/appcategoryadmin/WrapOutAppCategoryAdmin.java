package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.AppCategoryAdmin;

@Wrap(AppCategoryAdmin.class)
public class WrapOutAppCategoryAdmin extends AppCategoryAdmin {
	private static final long serialVersionUID = -5076990764713538973L;
	public static final List<String> Excludes = new ArrayList<String>();

}