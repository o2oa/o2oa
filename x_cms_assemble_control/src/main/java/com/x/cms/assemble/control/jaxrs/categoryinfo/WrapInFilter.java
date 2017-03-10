package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.CategoryInfo;

@Wrap( CategoryInfo.class)
public class WrapInFilter extends GsonPropertyObject {

	@EntityFieldDescribe( "作为过滤条件的CMS应用ID列表, 可多个, String数组." )
	private List<String> appIdList;
	
	@EntityFieldDescribe( "作为过滤条件的CMS分类ID列表, 可多个, String数组." )
	private List<String> categoryIdList;

	@EntityFieldDescribe( "作为过滤条件的创建者姓名列表, 可多个, String数组." )
	private List<String> creatorList;

	@EntityFieldDescribe( "作为过滤条件的CMS应用关键字, 通常是应用名称, String, 模糊查询." )
	private String key;

	public List<String> getAppIdList() {
		return appIdList;
	}

	public void setAppIdList(List<String> appIdList) {
		this.appIdList = appIdList;
	}

	public List<String> getCategoryIdList() {
		return categoryIdList;
	}

	public void setCategoryIdList(List<String> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}

	public List<String> getCreatorList() {
		return creatorList;
	}

	public void setCreatorList(List<String> creatorList) {
		this.creatorList = creatorList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
