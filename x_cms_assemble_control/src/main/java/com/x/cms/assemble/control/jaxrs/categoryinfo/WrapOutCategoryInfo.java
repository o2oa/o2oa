package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.CategoryInfo;

@Wrap( CategoryInfo.class )
public class WrapOutCategoryInfo extends CategoryInfo {
	
	private static final long serialVersionUID = -5076990764713538973L;
	
	public static List<String> Excludes = new ArrayList<String>();

}