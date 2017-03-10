package com.x.cms.assemble.control.jaxrs.documentpermission;

import java.util.List;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.Document;

@Wrap( Document.class )
public class WrapInDocumentSearchFilter {
	
	@EntityFieldDescribe( "作为过滤条件的CMS应用ID列表, 可多个, String数组." )
	private List<String> appIdList;
	
	@EntityFieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
	private List<String> appAliasList;
	
	@EntityFieldDescribe( "作为过滤条件的CMS分类ID列表, 可多个, String数组." )
	private List<String> categoryIdList;

	@EntityFieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
	private List<String> categoryAliasList;

	@EntityFieldDescribe( "作为过滤条件的创建者姓名列表, 可多个, String数组." )
	private List<String> creatorList;

	@EntityFieldDescribe( "作为过滤条件的文档状态列表, 可多个, String数组." )
	private List<String> statusList;
	
	@EntityFieldDescribe( "作为过滤条件的文档发布者姓名, 可多个, String数组." )
	private List<String> publisherList;
	
	@EntityFieldDescribe( "创建日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd." )
	private List<String> createDateList;	//
	
	@EntityFieldDescribe( "发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd." )
	private List<String> publishDateList;	//

	private String orderField = "publishTime";
	
	private String orderType = "DESC";

	@EntityFieldDescribe( "作为过滤条件的CMS文档关键字, 通常是标题, String, 模糊查询." )
	private String title;

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

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public List<String> getPublisherList() {
		return publisherList;
	}

	public void setPublisherList(List<String> publisherList) {
		this.publisherList = publisherList;
	}

	public List<String> getCreateDateList() {
		return createDateList;
	}

	public void setCreateDateList(List<String> createDateList) {
		this.createDateList = createDateList;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getPublishDateList() {
		return publishDateList;
	}

	public void setPublishDateList(List<String> publishDateList) {
		this.publishDateList = publishDateList;
	}

	public String getOrderField() {
		return orderField;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public List<String> getAppAliasList() {
		return appAliasList;
	}

	public List<String> getCategoryAliasList() {
		return categoryAliasList;
	}

	public void setAppAliasList(List<String> appAliasList) {
		this.appAliasList = appAliasList;
	}

	public void setCategoryAliasList(List<String> categoryAliasList) {
		this.categoryAliasList = categoryAliasList;
	}

}
