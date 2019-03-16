package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.assemble.control.jaxrs.view.exception.ExceptionViewAccessDenied;
import com.x.cms.assemble.control.jaxrs.view.exception.ExceptionViewDataQueryViewIdEmpty;
import com.x.cms.assemble.control.jaxrs.view.exception.ExceptionViewDateQueryCategoryIdEmpty;
import com.x.cms.assemble.control.jaxrs.view.exception.ExceptionViewInfoProcess;
import com.x.cms.assemble.control.jaxrs.view.exception.ExceptionViewNotExists;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.View;

public class ActionListNextPageViewData extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListNextPageViewData.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String lastDocId, Integer pageSize, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		SearchFilter searchFilter = new SearchFilter();
		Wi wi = null;
		Wo wo = null;
		WoDocument woDocument = new WoDocument();
		List<String> viewableCatagories = null;
		List<String> queryCategoryIdList = new ArrayList<>();
		List<String> documentStatusList = new ArrayList<>();
		List<String> viewAbleDocIds = null;
		List<Document> documentList = null;
		List<String> viewIdsForCategory = null;
		Long documentCount = 0L;
		Map<String, Object> condition = new HashMap<String, Object>();
		View view = null;
		Boolean check = true;
		Boolean isManager = false;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName =  effectivePerson.getDistinguishedName();
		List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
		List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );		
		
		if( pageSize <= 0){
			pageSize = 12;
		}
		
		if( "(0)".equals( lastDocId ) || StringUtils.isEmpty( lastDocId ) ){
			lastDocId = null;
		}
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null );
		}
		
		if( StringUtils.isEmpty( wi.getCategoryId() ) ) {
			check = false;
			Exception exception = new ExceptionViewDateQueryCategoryIdEmpty();
			result.error( exception );
		}else {
			condition.put( "categoryId", wi.getCategoryId() );
			queryCategoryIdList.add( wi.getCategoryId() );
		}
		
		if( StringUtils.isEmpty( wi.getViewId() ) ) {
			check = false;
			check = false;
			Exception exception = new ExceptionViewDataQueryViewIdEmpty();
			result.error( exception );
		}else {
			condition.put( "viewId", wi.getViewId() );
		}
		
		try {
			isManager = userManagerService.isManager(request, effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionViewInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( check ){
			try{
				view = viewServiceAdv.get( wi.getViewId() );
				if( view == null ){
					check = false;
					Exception exception = new ExceptionViewNotExists( wi.getViewId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据ID查询列表视图信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try{
				//检查当前的Category是否能够访问该列表，Category与View的关联是否存在
				viewIdsForCategory = viewServiceAdv.listViewIdsWithCategoryId( wi.getCategoryId() );
				//判断分类与列表信息是否存在关联，如果不存在，则不允许访问
				if( ListTools.isEmpty( viewIdsForCategory ) || !viewIdsForCategory.contains( wi.getViewId() )) {
					check = false;
					Exception exception = new ExceptionViewAccessDenied( wi.getCategoryId(),  wi.getViewId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据分类ID查询列表视图ID信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//设置查询条件的默认值，如果不传入则使用列表设置的值
			if( StringUtils.isEmpty( wi.getOrderField() ) ) {
				wi.setOrderField(view.getOrderField());
			}
			if( StringUtils.isEmpty( wi.getOrderType() ) ) {
				wi.setOrderType(view.getOrderType());
			}
			if( StringUtils.isEmpty( wi.getSearchDocStatus() ) ) {
				wi.setSearchDocStatus( "published" );
			}
		}
		
		if( check ){
			if( StringUtils.isEmpty( wi.getOrderField() ) ){
				condition.put( "orderField", "publishTime" );
			}else{
				condition.put( "orderField", wi.getOrderField() );
			}
			if(  StringUtils.isEmpty( wi.getOrderType() )){
				condition.put( "orderType", "ASC" );
			}else{
				condition.put( "orderType", wi.getOrderType() );
			}
			if( StringUtils.isEmpty( wi.getSearchDocStatus() ) ){
				condition.put( "searchDocStatus", "published" );
				documentStatusList.add( "published" );
			}else{
				condition.put( "searchDocStatus", wi.getSearchDocStatus() );
				documentStatusList.add( wi.getSearchDocStatus() );
			}
		}
		
		//计算当前用户管理的所有栏目和分类
		if( check ){
			//根据权限，把用户传入的AppId和categoryId进行过滤，最终形成一个可以访问的allViewAbleCategoryIds
			try {				
				viewableCatagories = permissionQueryService.listViewableCategoryIdByPerson( personName, isAnonymous, unitNames, groupNames, null, queryCategoryIdList, null, wi.getDocumentType(), 10000, isManager );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据登录人员获取能管理的分类信息ID列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			searchFilter.setCategoryIdList( queryCategoryIdList );
			searchFilter.setStatusList( documentStatusList );
		}
		
		if( check ){
			//到documentPermission表里根据要求进行条件查询并且排序后，输出下一页的ID列表
			try {
				viewAbleDocIds = documentInfoServiceAdv.lisViewableDocIdsWithFilter( 
						searchFilter.getTitle(), 
						searchFilter.getAppIdList(), 
						searchFilter.getCategoryIdList(),
						searchFilter.getPublisherList(), 
						searchFilter.getCreateDateList(),
						searchFilter.getPublishDateList(),
						searchFilter.getStatusList(),
						personName,
						unitNames,
						groupNames,
						isManager,
						viewableCatagories, wi.getDocumentType(), 500);
				if( viewAbleDocIds == null ){
					viewAbleDocIds = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			if( viewAbleDocIds != null && !viewAbleDocIds.isEmpty() ){
				documentCount = Long.parseLong( viewAbleDocIds.size() + "" );
			}
		}
		
		if( check ){
			if( viewAbleDocIds != null && !viewAbleDocIds.isEmpty() ){
				try {
					documentList = viewServiceAdv.nextPageDocuemntView( lastDocId, pageSize, viewAbleDocIds, condition );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionViewInfoProcess( e, "系统在根据条件查询列表数据时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			//补充业务数据
			if( documentList != null && documentList.size() > 0 ){
				for( Document document : documentList ){
					wo = new Wo();
					try {
						woDocument = WoDocument.copier.copy( document );
						if( woDocument != null ) {
							if( woDocument.getCreatorPerson() != null && !woDocument.getCreatorPerson().isEmpty() ) {
								woDocument.setCreatorPersonShort( woDocument.getCreatorPerson().split( "@" )[0]);
							}
							if( woDocument.getCreatorUnitName() != null && !woDocument.getCreatorUnitName().isEmpty() ) {
								woDocument.setCreatorUnitNameShort( woDocument.getCreatorUnitName().split( "@" )[0]);
							}
							if( woDocument.getCreatorTopUnitName() != null && !woDocument.getCreatorTopUnitName().isEmpty() ) {
								woDocument.setCreatorTopUnitNameShort( woDocument.getCreatorTopUnitName().split( "@" )[0]);
							}
						}
						wo.setDocument( woDocument );
					} catch (Exception e) {
						Exception exception = new ExceptionViewInfoProcess( e, "系统在将文档信息转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					try {
						wo.setData( documentInfoServiceAdv.getDocumentData(document) );
					} catch (Exception e) {
						Exception exception = new ExceptionViewInfoProcess( e, "系统在根据文档信息获取文档数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					wos.add( wo );
				}
			}
			result.setData( wos );
			result.setCount( documentCount );
		}
		
		return result;
	}
	
	public class Wi extends GsonPropertyObject {
		
		@FieldDescribe("需要过滤文档的分类ID")
		private String categoryId;

		@FieldDescribe("需要查询的列表ID")
		private String viewId;
		
		@FieldDescribe("当前查询用于排序的列名，默认以列表配置为主")
		private String orderField;

		@FieldDescribe("当前排序方式：DESC|ASC，默认以列表配置为主")
		private String orderType;
		
		@FieldDescribe("查询文档类型")
		private String documentType;
		
		@FieldDescribe("当前需要查询的文档状态：published | draft")
		private String searchDocStatus;

		public String getOrderField() {
			return orderField;
		}

		public void setOrderField(String orderField) {
			this.orderField = orderField;
		}

		public String getOrderType() {
			return orderType;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getViewId() {
			return viewId;
		}

		public void setViewId(String viewId) {
			this.viewId = viewId;
		}

		public String getSearchDocStatus() {
			return searchDocStatus;
		}

		public void setSearchDocStatus(String searchDocStatus) {
			this.searchDocStatus = searchDocStatus;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

	}
	
	public static class SearchFilter {

		@FieldDescribe( "作为过滤条件的CMS应用ID列表, 可多个, String数组." )
		private List<String> appIdList = null;
		
		@FieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
		private List<String> appAliasList = null;
		
		@FieldDescribe( "作为过滤条件的CMS分类ID列表, 可多个, String数组." )
		private List<String> categoryIdList = null;

		@FieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
		private List<String> categoryAliasList = null;
		
		@FieldDescribe( "作为过滤条件的创建者姓名列表, 可多个, String数组." )
		private List<String> creatorList = null;

		@FieldDescribe( "作为过滤条件的文档状态列表, 可多个, String数组." )
		private List<String> statusList = null;
		
		@FieldDescribe( "作为过滤条件的文档发布者姓名, 可多个, String数组." )
		private List<String> publisherList = null;
		
		@FieldDescribe( "创建日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd." )
		private List<String> createDateList = null;
		
		@FieldDescribe( "发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd." )
		private List<String> publishDateList = null;

		private String orderField = "publishTime";
		
		private String orderType = "DESC";

		@FieldDescribe( "作为过滤条件的CMS文档关键字, 通常是标题, String, 模糊查询." )
		private String title = null;

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
	
	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "作为输出的CMS文档数据对象." )
		private WoDocument document;

		@FieldDescribe( "文档所有数据信息." )
		private Map<?, ?> data;

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument( WoDocument document) {
			this.document = document;
		}

		public Map<?, ?> getData() {
			return data;
		}

		public void setData(Map<?, ?> data) {
			this.data = data;
		}
	}
	
	public static class WoDocument extends Document {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("创建者姓名（简称）")
		private String creatorPersonShort = null;
		
		@FieldDescribe("创建者所属组织（简称）")
		private String creatorUnitNameShort = null;
		
		@FieldDescribe("创建者顶层组织（简称）")
		private String creatorTopUnitNameShort = null;

		public String getCreatorPersonShort() {
			return creatorPersonShort;
		}

		public String getCreatorUnitNameShort() {
			return creatorUnitNameShort;
		}

		public String getCreatorTopUnitNameShort() {
			return creatorTopUnitNameShort;
		}

		public void setCreatorPersonShort(String creatorPersonShort) {
			this.creatorPersonShort = creatorPersonShort;
		}

		public void setCreatorUnitNameShort(String creatorUnitNameShort) {
			this.creatorUnitNameShort = creatorUnitNameShort;
		}

		public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
			this.creatorTopUnitNameShort = creatorTopUnitNameShort;
		}		
	}
}