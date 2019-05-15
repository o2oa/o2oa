package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

import net.sf.ehcache.Element;

public class ActionListDraftNextWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListDraftNextWithFilter.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, String id, Integer count,
			JsonElement jsonElement, EffectivePerson effectivePerson) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<Document> documentList = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		String cacheKey = getCacheKeyFormWrapInFilter("draft", id, count, wi);
		Element element = cache.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData(wos);
		} else {
//			if (check) {
//				if (wi.getCategoryIdList() == null || wi.getCategoryIdList().isEmpty()) {
//					check = false;
//					Exception exception = new ExceptionDocumentCategoryIdEmpty();
//					result.error(exception);
//				}
//			}

			if (check) {
				try {
					documentList = documentInfoServiceAdv.listMyDraft(effectivePerson.getDistinguishedName(),
							wi.getCategoryIdList(), wi.getDocumentType() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "系统在查询用户草稿信息列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}

			if (check) {
				if (documentList != null) {
					try {
						wos = Wo.copier.copy(documentList);
						for (Wo wo : wos) {
							if (wo.getCreatorPerson() != null && !wo.getCreatorPerson().isEmpty()) {
								wo.setCreatorPersonShort(wo.getCreatorPerson().split("@")[0]);
							}
							if (wo.getCreatorUnitName() != null && !wo.getCreatorUnitName().isEmpty()) {
								wo.setCreatorUnitNameShort(wo.getCreatorUnitName().split("@")[0]);
							}
							if (wo.getCreatorTopUnitName() != null && !wo.getCreatorTopUnitName().isEmpty()) {
								wo.setCreatorTopUnitNameShort(wo.getCreatorTopUnitName().split("@")[0]);
							}
						}
						result.setCount(Long.parseLong(documentList.size() + ""));
						cache.put(new Element(cacheKey, wos));
						result.setData(wos);
					} catch (Exception e) {
						Exception exception = new ExceptionDocumentInfoProcess(e, "系统在将分页查询结果转换为可输出的数据信息时发生异常。");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}

		return result;
	}

	private String getCacheKeyFormWrapInFilter(String flag, String id, Integer count, Wi wi) {

		String cacheKey = ApplicationCache.concreteCacheKey(id, count, flag);

		if (wi.getTitle() != null && !wi.getTitle().isEmpty()) {
			cacheKey = ApplicationCache.concreteCacheKey(cacheKey, wi.getTitle());
		}
		if (wi.getOrderType() != null && !wi.getOrderType().isEmpty()) {
			cacheKey = ApplicationCache.concreteCacheKey(cacheKey, wi.getOrderType());
		}
		if (wi.getAppIdList() != null && !wi.getAppIdList().isEmpty()) {
			for (String key : wi.getAppIdList()) {
				cacheKey = ApplicationCache.concreteCacheKey(cacheKey, key);
			}
		}
		if (wi.getCategoryIdList() != null && !wi.getCategoryIdList().isEmpty()) {
			for (String key : wi.getCategoryIdList()) {
				cacheKey = ApplicationCache.concreteCacheKey(cacheKey, key);
			}
		}
		if (wi.getCreateDateList() != null && !wi.getCreateDateList().isEmpty()) {
			for (String key : wi.getCreateDateList()) {
				cacheKey = ApplicationCache.concreteCacheKey(cacheKey, key);
			}
		}
		if (wi.getPublishDateList() != null && !wi.getPublishDateList().isEmpty()) {
			for (String key : wi.getPublishDateList()) {
				cacheKey = ApplicationCache.concreteCacheKey(cacheKey, key);
			}
		}
		if (wi.getPublisherList() != null && !wi.getPublisherList().isEmpty()) {
			for (String key : wi.getPublisherList()) {
				cacheKey = ApplicationCache.concreteCacheKey(cacheKey, key);
			}
		}
		if (wi.getStatusList() != null && !wi.getStatusList().isEmpty()) {
			for (String key : wi.getStatusList()) {
				cacheKey = ApplicationCache.concreteCacheKey(cacheKey, key);
			}
		}
		if (wi.getDocumentType() != null && !wi.getDocumentType().isEmpty()) {
			cacheKey = ApplicationCache.concreteCacheKey(cacheKey, wi.getDocumentType());
		}
		return cacheKey;
	}

	public static class Wi {

		@FieldDescribe("作为过滤条件的CMS应用ID列表, 可多个, String数组.")
		private List<String> appIdList;

		@FieldDescribe("作为过滤条件的CMS应用别名列表, 可多个, String数组.")
		private List<String> appAliasList;

		@FieldDescribe("作为过滤条件的CMS分类ID列表, 可多个, String数组.")
		private List<String> categoryIdList;

		@FieldDescribe("作为过滤条件的CMS应用别名列表, 可多个, String数组.")
		private List<String> categoryAliasList;

		@FieldDescribe("作为过滤条件的创建者姓名列表, 可多个, String数组.")
		private List<String> creatorList;

		@FieldDescribe("作为过滤条件的文档状态列表, 可多个, String数组.")
		private List<String> statusList;

		@FieldDescribe("作为过滤条件的文档发布者姓名, 可多个, String数组.")
		private List<String> publisherList;

		@FieldDescribe("创建日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd.")
		private List<String> createDateList; //

		@FieldDescribe("发布日期列表，可以传入1个(开始时间)或者2个(开始和结束时间), String, yyyy-mm-dd.")
		private List<String> publishDateList; //
		
		@FieldDescribe( "文档类型：全部 | 信息 | 数据" )
		private String documentType = "信息";

		private String orderField = "publishTime";

		private String orderType = "DESC";

		@FieldDescribe("作为过滤条件的CMS文档关键字, 通常是标题, String, 模糊查询.")
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

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

	}

	public static class Wo extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Document, Wo> copier = WrapCopierFactory.wo(Document.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		/**
		 * 只作显示用
		 */
		private String creatorPersonShort = null;

		private String creatorUnitNameShort = null;

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