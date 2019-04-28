package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;

public class ActionListNextWithFilter_NEW extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListNextWithFilter_NEW.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, String id, Integer count, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		
		Long total = 0L;
		Wi wi = null;		
		List<Wo> wos = new ArrayList<>();
		List<Document> documentList = null;
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
		if (count == 0) {
			count = 20;
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
						personName, isAnonymous, manager, 1000
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
						wi.getImportBatchNames(), personNames, unitNames, groupNames, manager, lastedPublishTime );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在获取用户可查询到的文档数据条目数量时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				documentList = documentInfoServiceAdv.listNextWithCondition( id, count, queryCategoryIds, wi.getTitle(), 
						wi.getPublisherList(), wi.getCreateDateList(), wi.getPublishDateList(), wi.getStatusList(), wi.getDocumentType(), 
						wi.getCreatorUnitNameList(),
						wi.importBatchNames, personNames, unitNames, groupNames, wi.getOrderField(), wi.getOrderType(), manager, lastedPublishTime );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据用户可访问的文档ID列表对文档进行分页查询时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if ( documentList != null ) {
				Wo wo = null;
				for( Document document : documentList ) {					
					try {
						wo = Wo.copier.copy( document );
						
						if( wo.getCreatorPerson() != null && !wo.getCreatorPerson().isEmpty() ) {
							wo.setCreatorPersonShort( wo.getCreatorPerson().split( "@" )[0]);
						}
						if( wo.getCreatorUnitName() != null && !wo.getCreatorUnitName().isEmpty() ) {
							wo.setCreatorUnitNameShort( wo.getCreatorUnitName().split( "@" )[0]);
						}
						if( wo.getCreatorTopUnitName() != null && !wo.getCreatorTopUnitName().isEmpty() ) {
							wo.setCreatorTopUnitNameShort( wo.getCreatorTopUnitName().split( "@" )[0]);
						}
						if( wi.needData ) {
							wo.setData( documentInfoServiceAdv.getDocumentData( document ) );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionDocumentInfoProcess(e, "系统获取文档数据内容信息时发生异常。Id:" + document.getCategoryId());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
					
					wos.add( wo );
				}
			}
		}

		result.setCount(total);
		result.setData(wos);
		return result;
	}
	
	public class DocumentCacheForFilter {

		private Long total = 0L;
		
		private List<Wo> documentList = null;

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getDocumentList() {
			return documentList;
		}

		public void setDocumentList(List<Wo> documentList) {
			this.documentList = documentList;
		}	
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

		
		public Integer getMinutes() {
			return minutes;
		}

		public void setMinutes(Integer minutes) {
			this.minutes = minutes;
		}

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
	}
	
	public static class Wo {
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<Document, Wo> copier = WrapCopierFactory.wo(Document.class, Wo.class, null,JpaObject.FieldsInvisible);
		
		@FieldDescribe( "文档ID." )
		private String id = Document.createId();

		@FieldDescribe( "创建时间" )
		private Date createTime;
		
		@FieldDescribe("文档发布时间")
		private Date publishTime;

		@FieldDescribe( "最近修改时间" )
		private Date updateTime;
		
		@FieldDescribe("文档摘要")
		private String summary;
		
		@FieldDescribe("文档标题")
		private String title;
		
		@FieldDescribe("文件导入的批次号：一般是分类ID+时间缀")
		private String importBatchName;
		
		@FieldDescribe("说明备注，可以填写说明信息，如导入信息检验失败原因等")
		private String description = null;
		
		@FieldDescribe( "分类唯一标识" )
		private String categoryAlias;
		
		@FieldDescribe( "分类名称" )
		private String categoryName;
		
		@FieldDescribe( "栏目ID" )
		private String appId;
		
		@FieldDescribe( "分类ID" )
		private String categoryId;
		
		@FieldDescribe("创建人，可能为空，如果由系统创建。")
		private String creatorPerson;

		@FieldDescribe("创建人组织名称，可能为空，如果由系统创建。")
		private String creatorUnitName;

		@FieldDescribe("创建人顶层组织名称，可能为空，如果由系统创建。")
		private String creatorTopUnitName;

		@FieldDescribe("文档状态: published | draft")
		private String docStatus = "draft";
		
		@FieldDescribe("文档被查看次数")
		private Long viewCount = 0L;
		
		@FieldDescribe("是否含有首页图片")
		private Boolean hasIndexPic = false;

		@FieldDescribe("首页图片列表")
		private List<String> pictureList;		
		
		@FieldDescribe("文档所有数据信息.")
		private Map<?, ?> data;
		
		/**
		 * 只作显示用
		 */
		private String creatorPersonShort = null;
		
		private String creatorUnitNameShort = null;
		
		private String creatorTopUnitNameShort = null;

		
		public String getImportBatchName() {
			return importBatchName;
		}

		public String getDescription() {
			return description;
		}

		public void setImportBatchName(String importBatchName) {
			this.importBatchName = importBatchName;
		}

		public void setDescription(String description) {
			this.description = description;
		}

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

		public String getId() {
			return id;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public Date getPublishTime() {
			return publishTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public String getSummary() {
			return summary;
		}

		public String getTitle() {
			return title;
		}

		public String getCategoryAlias() {
			return categoryAlias;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}
		
		public String getDocStatus() {
			return docStatus;
		}

		public Long getViewCount() {
			return viewCount;
		}

		public Boolean getHasIndexPic() {
			return hasIndexPic;
		}

		public List<String> getPictureList() {
			return pictureList;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public void setPublishTime(Date publishTime) {
			this.publishTime = publishTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setCategoryAlias(String categoryAlias) {
			this.categoryAlias = categoryAlias;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public void setViewCount(Long viewCount) {
			this.viewCount = viewCount;
		}

		public void setHasIndexPic(Boolean hasIndexPic) {
			this.hasIndexPic = hasIndexPic;
		}

		public void setPictureList(List<String> pictureList) {
			this.pictureList = pictureList;
		}

		public String getCategoryName() {
			return categoryName;
		}

		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}

		public String getCreatorUnitName() {
			return creatorUnitName;
		}

		public String getCreatorTopUnitName() {
			return creatorTopUnitName;
		}

		public void setCreatorUnitName(String creatorUnitName) {
			this.creatorUnitName = creatorUnitName;
		}

		public void setCreatorTopUnitName(String creatorTopUnitName) {
			this.creatorTopUnitName = creatorTopUnitName;
		}

		public String getAppId() {
			return appId;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public Map<?, ?> getData() {
			return data;
		}

		public void setData(Map<?, ?> data) {
			this.data = data;
		}		
	}
}