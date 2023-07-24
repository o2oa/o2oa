package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

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
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.editable(effectivePerson, null)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(), "all", "all");
			}
			EntityManager em = emc.get(ApplicationDict.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			;
			List<Wo> wos = emc.fetchDescPaging(ApplicationDict.class, Wo.copier, p, page, size,
					ApplicationDict.sequence_FIELDNAME);
			wos.stream().forEach(wo -> {
				try {
					Application app = emc.find(wo.getApplication(), Application.class);
					if (app != null) {
						wo.setApplicationName(app.getName());
						wo.setApplicationAlias(app.getAlias());
					}
				} catch (Exception e) {
					logger.error(e);
				}
			});
			result.setData(wos);
			result.setCount(emc.count(ApplicationDict.class, p));
			return result;
		}
	}

	public static class Wo extends ApplicationDict {

		private static final long serialVersionUID = -192812264880120309L;

		static WrapCopier<ApplicationDict, Wo> copier = WrapCopierFactory.wo(ApplicationDict.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("应用名称.")
		private String applicationName;

		@FieldDescribe("应用别名.")
		private String applicationAlias;

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getApplicationAlias() {
			return applicationAlias;
		}

		public void setApplicationAlias(String applicationAlias) {
			this.applicationAlias = applicationAlias;
		}
	}
}
