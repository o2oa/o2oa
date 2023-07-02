package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.jaxrs.document.BaseAction;
import com.x.cms.core.entity.CategoryInfo;

public class ActionQueryListWithFilterPaging extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListWithFilterPaging.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, Integer page, Integer size, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		Wi wi = null;
		List<Wo> wraps = null;
		List<Wo> wraps_out = new ArrayList<>();
		Boolean check = true;

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCategoryInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (null != wi.getAppIdList() && !wi.getAppIdList().isEmpty()) {
				ins.put("appId", wi.getAppIdList());
			}
			if (null != wi.getCategoryIdList() && !wi.getCategoryIdList().isEmpty()) {
				ins.put("id", wi.getCategoryIdList());
			}
			if (null != wi.getCreatorList() && !wi.getCreatorList().isEmpty()) {
				ins.put("creatorPerson", wi.getCreatorList());
			}
			if (StringUtils.isNotEmpty(wi.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("title", key);
				}
			}
		}

		if (check) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), wi.getAppIdList(), wi.getKey(), "allCategoryInApps" );
			Optional<?> optional = CacheManager.get( cacheCategory, cacheKey );
			if (optional.isPresent()) {
				wraps = (List<Wo>)optional.get();
				result.setData( wraps );
				result.setCount( Long.parseLong(wraps.size() + "" ) );
			} else {
				try {
					//一次性取出所有的分类信息，最大2000条，在内存里进行分页
					result = this.standardListNext( Wo.copier, "(0)", 5000, "sequence", equals, null, likes, ins, null, null, null, null, true, DESC);
					CacheManager.put(cacheCategory, cacheKey, result.getData() );
				} catch (Exception e) {
					check = false;
					result.error(e);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			wraps = result.getData();

			if( page <= 0 ){
				page = 1;
			}
			if( size <= 0 ){
				size = 20;
			}
			int startIndex = ( page - 1 ) * size;
			int endIndex = page * size;
			int i = 0;

			for( ; wraps != null && i< wraps.size(); i++ ){
				if( i >= startIndex && i < endIndex ){
					wraps_out.add( wraps.get( i ) );
				}
			}
			result.setData( wraps_out );
			result.setSize(  Long.parseLong(size + "" ));
		}
		return result;
	}	


	public static class Wo extends CategoryInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo(CategoryInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		@FieldDescribe("扩展信息JSON内容")
		private String extContent = null;

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("作为过滤条件的信息内容管理应用ID列表, 可多个, String数组.")
		private List<String> appIdList;

		@FieldDescribe("作为过滤条件的内容管理分类ID列表, 可多个, String数组.")
		private List<String> categoryIdList;

		@FieldDescribe("作为过滤条件的创建者姓名列表, 可多个, String数组.")
		private List<String> creatorList;

		@FieldDescribe("作为过滤条件的内容管理应用关键字, 通常是应用名称, String, 模糊查询.")
		private String key;

		@FieldDescribe("文档类型：全部 | 信息 | 数据")
		private String documentType = "信息";

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

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}
	}
}