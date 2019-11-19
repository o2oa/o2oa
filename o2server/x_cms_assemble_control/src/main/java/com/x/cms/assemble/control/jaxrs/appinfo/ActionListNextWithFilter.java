package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.base.core.project.jaxrs.InTerms;
import com.x.base.core.project.jaxrs.LikeTerms;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListNextWithFilter.class);

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
			Exception exception = new ExceptionAppInfoProcess(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wi == null) {
				result.setCount(0L);
				result.setData(new ArrayList<Wo>());
				return result;
			}
			if (id == null) {
				id = "(0)";
			}
			if (count == null) {
				count = 20;
			}
			if ((null != wi.getAppIdList()) && (!wi.getAppIdList().isEmpty())) {
				ins.put("id", wi.getAppIdList());
			}
			if ((null != wi.getCreatorList()) && (!wi.getCreatorList().isEmpty())) {
				ins.put("creatorUid", wi.getCreatorList());
			}
			if ( StringUtils.isNotEmpty(wi.getKey()) ) {
				String key = StringUtils.trim(StringUtils.replace(wi.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("appName", key);
				}
			}
			try {
				result = this.standardListNext(Wo.copier, id, count, "sequence", equals, null, likes, ins, null, null,
						null, null, true, DESC);
			} catch (Exception e) {
				result.error(e);
				logger.warn("系统在分页查询栏目信息列表时发生异常。");
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public class Wi {

		@FieldDescribe("作为过滤条件的CMS应用ID, 可多个, String数组.")
		private List<String> appIdList;

		@FieldDescribe("作为过滤条件的创建者姓名, 可多个, String数组.")
		private List<String> creatorList;

		@FieldDescribe("作为过滤条件的CMS应用关键字, 通常是应用名称, String, 模糊查询.")
		private String key;

		@FieldDescribe("文档类型：全部 | 信息 | 数据")
		private String documentType = "信息";

		public List<String> getAppIdList() {
			return appIdList;
		}

		public void setAppIdList(List<String> appIdList) {
			this.appIdList = appIdList;
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