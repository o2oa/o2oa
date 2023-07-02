package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.element.Form;

public class ActionListPrevWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPrevWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, String appId, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		Wi wrapIn = null;
		Boolean check = true;
		WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null, Wo.excludes);
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try {
				equals.put("appId", appId);
				if ((null != wrapIn.getCategoryIdList()) && (!wrapIn.getCategoryIdList().isEmpty())) {
					equals.put("categoryId", wrapIn.getCategoryIdList().get(0));
				}
				if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
					equals.put("creatorUid", wrapIn.getCreatorList().get(0));
				}
				if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
					equals.put("docStatus", wrapIn.getStatusList().get(0));
				}
				if (StringUtils.isNotEmpty(wrapIn.getKey())) {
					String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
					if (StringUtils.isNotEmpty(key)) {
						likes.put("title", key);
					}
				}
				result = this.standardListPrev(copier, id, count, "sequence", equals, null, likes, null, null, null,
						null, null, true, DESC);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public static class Wo extends Form {

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用于过滤条件的栏目ID列表.")
		private List<String> appIdList;

		@FieldDescribe("用于过滤条件的分类ID列表.")
		private List<String> categoryIdList;

		@FieldDescribe("用于过滤条件的创建者列表.")
		private List<String> creatorList;

		@FieldDescribe("用于过滤条件的文档状态列表.")
		private List<String> statusList;

		@FieldDescribe("用于标题搜索的关键字.")
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

		public List<String> getStatusList() {
			return statusList;
		}

		public void setStatusList(List<String> statusList) {
			this.statusList = statusList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}
}
