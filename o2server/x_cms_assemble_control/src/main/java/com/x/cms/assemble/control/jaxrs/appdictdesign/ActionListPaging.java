package com.x.cms.assemble.control.jaxrs.appdictdesign;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.AppDict;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(AppDict.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			;
			List<Wo> wos = emc.fetchDescPaging(AppDict.class, Wo.copier, p, page, size, AppDict.sequence_FIELDNAME);
			wos.stream().forEach(wo -> {
				try {
					AppInfo appInfo = emc.find(wo.getAppId(), AppInfo.class);
					if (appInfo != null) {
						wo.setAppName(appInfo.getAppName());
						wo.setAppAlias(appInfo.getAppAlias());
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			result.setData(wos);
			result.setCount(emc.count(AppDict.class, p));
			return result;
		}
	}

	public static class Wo extends AppDict {

		private static final long serialVersionUID = -2252387711917161807L;

		static WrapCopier<AppDict, Wo> copier = WrapCopierFactory.wo(AppDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("应用名称.")
		private String appName;

		@FieldDescribe("应用别名.")
		private String appAlias;

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getAppAlias() {
			return appAlias;
		}

		public void setAppAlias(String appAlias) {
			this.appAlias = appAlias;
		}
	}
}
