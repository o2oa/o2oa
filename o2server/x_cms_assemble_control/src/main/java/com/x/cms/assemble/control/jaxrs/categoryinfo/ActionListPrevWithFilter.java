package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

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
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;

public class ActionListPrevWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		Wi wi = null;
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
			try {
				result = this.standardListPrev(Wo.copier, id, count, "sequence", equals, null, likes, ins, null, null,
						null, null, true, DESC);
			} catch (Exception e) {
				result.error(e);
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}

		return result;
	}

	public static class Wo extends CategoryInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo(CategoryInfo.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));

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