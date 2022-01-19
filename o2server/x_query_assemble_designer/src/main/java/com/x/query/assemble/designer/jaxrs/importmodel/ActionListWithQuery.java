package com.x.query.assemble.designer.jaxrs.importmodel;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;

class ActionListWithQuery extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class );
			if (null == query) {
				throw new ExceptionEntityNotExist(flag, Query.class);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query.getName());
			}
			List<ImportModel> os = emc.listEqual(ImportModel.class, ImportModel.query_FIELDNAME, query.getId());
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends ImportModel {

		private static final long serialVersionUID = 1302228725969026941L;

		static WrapCopier<ImportModel, Wo> copier = WrapCopierFactory.wo(ImportModel.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
