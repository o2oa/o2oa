package com.x.query.assemble.designer.jaxrs.view;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;

class ActionListWithQuery extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class );
			if (null == query) {
				throw new ExceptionQueryNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			List<View> os = business.view().listWithQueryObject(query.getId());
			List<Wo> wos = Wo.copier.copy(os);
			wos = business.view().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends View {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<View, Wo> copier = WrapCopierFactory.wo(View.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
