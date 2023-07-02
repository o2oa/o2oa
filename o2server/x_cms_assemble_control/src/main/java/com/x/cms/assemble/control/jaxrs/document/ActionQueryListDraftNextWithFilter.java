package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;

public class ActionQueryListDraftNextWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListDraftNextWithFilter.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, String id, Integer count, JsonElement jsonElement, EffectivePerson effectivePerson) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<Document> documentList = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id, count, wi );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wos = ( List<Wo> ) optional.get();
			result.setData(wos);
		} else {
			if (check) {
				try {
					documentList = documentQueryService.listMyDraft( effectivePerson.getDistinguishedName(), wi.getCategoryIdList(), wi.getDocumentType() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess( e, "系统在查询用户草稿信息列表时发生异常。" );
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
						CacheManager.put(cacheCategory, cacheKey, wos );
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

	public static class Wi {

		@FieldDescribe("作为过滤条件的CMS分类ID列表, 可多个, String数组.")
		private List<String> categoryIdList;

		@FieldDescribe( "文档类型：全部 | 信息 | 数据" )
		private String documentType = "信息";

		public List<String> getCategoryIdList() {
			return categoryIdList;
		}

		public void setCategoryIdList(List<String> categoryIdList) {
			this.categoryIdList = categoryIdList;
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

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Document, Wo> copier = WrapCopierFactory.wo(Document.class, Wo.class, null,
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
