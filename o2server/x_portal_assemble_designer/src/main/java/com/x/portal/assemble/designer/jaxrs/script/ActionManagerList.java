package com.x.portal.assemble.designer.jaxrs.script;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;

class ActionManagerList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionManagerList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos;
			if (ListTools.isEmpty(wi.getAppIdList())) {
				wos = emc.fetchAll(Script.class, Wo.copier);
			} else {
				wos = emc.fetchIn(Script.class, Wo.copier, Script.portal_FIELDNAME, wi.getAppIdList());
			}
			final List<Wo> resWos = new ArrayList<>();
			wos.stream().forEach(wo -> {
				try {
					Portal portal = emc.find(wo.getPortal(), Portal.class);
					if (portal != null) {
						wo.setAppId(portal.getId());
						wo.setAppName(portal.getName());
					}
				} catch (Exception e) {
					logger.error(e);
				}
				if (StringUtils.isNotBlank(wi.getKeyword())) {
					if (StringTools.matchKeyword(wi.getKeyword(), wo.getText(), wi.getCaseSensitive(),
							wi.getMatchWholeWord(), wi.getMatchRegExp())) {
						resWos.add(wo);
					}
				} else {
					resWos.add(wo);
				}
			});
			wos.clear();
			result.setData(resWos);
			result.setCount((long) resWos.size());
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("搜索关键字.")
		private String keyword;
		@FieldDescribe("是否区分大小写.")
		private Boolean caseSensitive;
		@FieldDescribe("是否全字匹配.")
		private Boolean matchWholeWord;
		@FieldDescribe("是否正则表达式匹配.")
		private Boolean matchRegExp;
		@FieldDescribe("应用ID列表.")
		private List<String> appIdList = new ArrayList<>();

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public Boolean getCaseSensitive() {
			return caseSensitive;
		}

		public void setCaseSensitive(Boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
		}

		public Boolean getMatchWholeWord() {
			return matchWholeWord;
		}

		public void setMatchWholeWord(Boolean matchWholeWord) {
			this.matchWholeWord = matchWholeWord;
		}

		public Boolean getMatchRegExp() {
			return matchRegExp;
		}

		public void setMatchRegExp(Boolean matchRegExp) {
			this.matchRegExp = matchRegExp;
		}

		public List<String> getAppIdList() {
			return appIdList;
		}

		public void setAppIdList(List<String> appIdList) {
			this.appIdList = appIdList;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -8095369685452823624L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class,
				JpaObject.singularAttributeField(Script.class, true, false), null);

		@FieldDescribe("应用Id.")
		private String appId;

		@FieldDescribe("应用名称.")
		private String appName;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}
	}
}
