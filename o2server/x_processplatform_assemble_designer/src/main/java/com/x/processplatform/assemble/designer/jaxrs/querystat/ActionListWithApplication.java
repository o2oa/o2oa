package com.x.processplatform.assemble.designer.jaxrs.querystat;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionListWithApplication extends BaseAction {
	
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationId);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			List<String> ids = business.queryStat().listWithApplication(applicationId);
			List<QueryStat> os = emc.list(QueryStat.class, ids);
			List<Wo> wos = Wo.copier.copy(os);
			wos = business.queryStat().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends QueryStat {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<QueryStat, Wo> copier = WrapCopierFactory.wo(QueryStat.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
