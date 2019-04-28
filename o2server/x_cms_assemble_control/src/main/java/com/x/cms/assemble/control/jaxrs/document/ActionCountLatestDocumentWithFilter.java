package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionCountLatestDocumentWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionCountLatestDocumentWithFilter.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		
		Long total = 0L;
		Wi wi = null;
		Wo wo = new Wo();
		List<String> queryCategoryIds = new ArrayList<>();
		
		Boolean manager = false;
		Boolean check = true;
		
		List<String> personNames = new ArrayList<>();
		List<String> unitNames = null;
		List<String> groupNames = null;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();
		Integer minutes = null;		
		Date lastedPublishTime = null;
		
		personNames.add( "所有人" );
		personNames.add( personName );
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (wi == null) {
			wi = new Wi();
		}
		
		if( StringUtils.isEmpty( wi.getDocumentType() )) {
			wi.setDocumentType( "信息" );
		}
		
		try {
			manager = userManagerService.isManager( request, effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			// 根据权限，把用户传入的AppId和categoryId进行过滤，最终形成一个可访问的queryCategoryIds
			try {
				queryCategoryIds = listAllViewAbleCategoryIds( 
						wi.getAppIdList(),  wi.getAppAliasList(), wi.getCategoryIdList(), wi.getCategoryAliasList(), wi.getDocumentType(), 
						personName, isAnonymous, manager, 500
				);
				if ( queryCategoryIds == null) {
					queryCategoryIds = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据应用栏目列表和分类列表计算可访问的分类ID列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if ( ListTools.isEmpty( queryCategoryIds )) {
				// 看看是不是从所有的栏目和分类里取，如果不是，则添加无可见分类，如果是，则整体排序
				if ( ListTools.isEmpty( wi.getAppIdList() ) && ListTools.isEmpty( wi.getAppAliasList() )
						&& ListTools.isEmpty( wi.getCategoryIdList() ) && ListTools.isEmpty( wi.getCategoryAliasList() )
				) {
					// 手机办公首页，是从所有文档里直接查询的，没有带查询条件, 查询所有
				} else {
					queryCategoryIds = new ArrayList<>();
					queryCategoryIds.add("无可见分类");
				}
			}
		}

		if( check ) {
			try {
				unitNames = userManagerService.listUnitNamesWithPerson( personName );
				groupNames = userManagerService.listGroupNamesByPerson( personName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			minutes = wi.getMinutes();
			if ( minutes == null || minutes <= 0) {
				lastedPublishTime = null;
			}else {
				lastedPublishTime = new Date ( new Date().getTime() - minutes*60*1000L );
			}
		
			// 从数据库中查询符合条件的对象总数
			try {
				total = documentInfoServiceAdv.countWithCondition( queryCategoryIds, wi.getTitle(), 
						wi.getPublisherList(), wi.getCreateDateList(), wi.getPublishDateList(), wi.getStatusList(), wi.getDocumentType(), 
						wi.getCreatorUnitNameList(),
						wi.getImportBatchNames(), personNames, unitNames, groupNames,  manager, lastedPublishTime );
				if( total == null || total < 0 ) {
					total = 0L;
				}
				wo.setDocCount(total);
				result.setCount(total);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在获取用户可查询到的文档数据条目数量时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		
		result.setData(wo);
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "只查询minutes分钟之类发布的文档，值为null或者为0时不作限制" )
		private Integer minutes = null;

		@FieldDescribe( "作为过滤条件的CMS应用ID列表, 可多个, String数组." )
		private List<String> appIdList;
		
		@FieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
		private List<String> appAliasList;
		
		@FieldDescribe( "作为过滤条件的CMS分类ID列表, 可多个, String数组." )
		private List<String> categoryIdList;

		@FieldDescribe( "作为过滤条件的CMS应用别名列表, 可多个, String数组." )
		private List<String> categoryAliasList;
		
		@FieldDescribe( "作为过滤条件的创建者姓名列表, 可多个, String数组." )
		private List<String> creatorList;

		@FieldDescribe( "作为过滤条件的文档状态列表, 可多个, String数组，值：published | draft | checking | error" )
		private List<String> statusList;
		
		@FieldDescribe( "作为过滤条件的文档发布者姓名, 可多个, String数组." )
		private List<String> publisherList;
		
		@FieldDescribe( "创建日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd." )
		private List<String> createDateList;	//
		
		@FieldDescribe( "发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd." )
		private List<String> publishDateList;	//
		
		@FieldDescribe( "作为过滤条件的发布者所属组织, 可多个, String数组." )
		private List<String> creatorUnitNameList;

		@FieldDescribe( "文档类型：全部 | 信息 | 数据" )
		private String documentType = "信息";

		@FieldDescribe( "文档导入的批次号：在导入文件时系统返回的批次号(数组)" )
		private List<String> importBatchNames = null;
		
		@FieldDescribe( "排序列名" )
		private String orderField = "publishTime";
		
		@FieldDescribe( "排序方式：ASC|DESC." )
		private String orderType = "DESC";
		
		@FieldDescribe( "是否需要查询数据，默认不查询." )
		private Boolean needData = false;

		@FieldDescribe( "作为过滤条件的CMS文档关键字, 通常是标题, String, 模糊查询." )
		private String title;

		public List<String> getCreatorUnitNameList() {
			return creatorUnitNameList;
		}

		public void setCreatorUnitNameList(List<String> creatorUnitNameList) {
			this.creatorUnitNameList = creatorUnitNameList;
		}

		public List<String> getAppIdList() {
			return appIdList == null?new ArrayList<>():appIdList;
		}

		public void setAppIdList(List<String> appIdList) {
			this.appIdList = appIdList;
		}

		public List<String> getCategoryIdList() {
			return categoryIdList == null?new ArrayList<>():categoryIdList;
		}

		public void setCategoryIdList(List<String> categoryIdList) {
			this.categoryIdList = categoryIdList;
		}

		public List<String> getCreatorList() {
			return creatorList == null?new ArrayList<>():creatorList;
		}

		public void setCreatorList(List<String> creatorList) {
			this.creatorList = creatorList;
		}

		public List<String> getStatusList() {
			return statusList == null?new ArrayList<>():statusList;
		}

		public void setStatusList(List<String> statusList) {
			this.statusList = statusList;
		}

		public List<String> getPublisherList() {
			return publisherList == null?new ArrayList<>():publisherList;
		}

		public void setPublisherList(List<String> publisherList) {
			this.publisherList = publisherList;
		}

		public List<String> getCreateDateList() {
			return createDateList == null?new ArrayList<>():createDateList;
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
			return publishDateList == null?new ArrayList<>():publishDateList;
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
			return appAliasList == null?new ArrayList<>():appAliasList;
		}

		public List<String> getCategoryAliasList() {
			return categoryAliasList == null?new ArrayList<>():categoryAliasList;
		}

		public void setAppAliasList(List<String> appAliasList) {
			this.appAliasList = appAliasList;
		}

		public void setCategoryAliasList(List<String> categoryAliasList) {
			this.categoryAliasList = categoryAliasList;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}		

		public List<String> getImportBatchNames() {
			return importBatchNames;
		}

		public void setImportBatchNames(List<String> importBatchNames) {
			this.importBatchNames = importBatchNames;
		}

		public Boolean getNeedData() {
			return needData;
		}

		public void setNeedData(Boolean needData) {
			this.needData = needData;
		}

		public Integer getMinutes() {
			return minutes;
		}

		public void setMinutes(Integer minutes) {
			this.minutes = minutes;
		}
		
	}
	
	public static class Wo {
		
		@FieldDescribe( "查询到的文档数量" )
		Long docCount = 0L;

		public Long getDocCount() {
			return docCount;
		}

		public void setDocCount(Long docCount) {
			this.docCount = docCount;
		}
		
	}
}