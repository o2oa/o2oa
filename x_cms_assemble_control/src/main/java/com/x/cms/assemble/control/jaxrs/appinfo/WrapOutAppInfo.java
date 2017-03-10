package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapOutCategoryInfo;
import com.x.cms.core.entity.AppInfo;

@Wrap( AppInfo.class )
public class WrapOutAppInfo extends AppInfo {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

	private List<WrapOutCategoryInfo> wrapOutCategoryList = null;

	public List<WrapOutCategoryInfo> getWrapOutCategoryList() {
		return wrapOutCategoryList;
	}

	public void setWrapOutCategoryList(List<WrapOutCategoryInfo> wrapOutCategoryList) {
		this.wrapOutCategoryList = wrapOutCategoryList;
	}
	
}