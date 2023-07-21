package com.x.query.assemble.surface.jaxrs.importmodel;

import java.util.ArrayList;
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
import com.x.base.core.project.tools.SortTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;

class ActionListWithQuery extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String queryFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Query query = business.pick(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag, Query.class);
			}
			for (String id : emc.idsEqual(ImportModel.class, ImportModel.query_FIELDNAME, query.getId())) {
				ImportModel o = business.pick(id, ImportModel.class);
				if (null != o) {
					if (business.readable(effectivePerson, o)) {
						wos.add(Wo.copier.copy(o));
					}
				}
			}
			SortTools.asc(wos, View.orderNumber_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends ImportModel {

		private static final long serialVersionUID = 6825258013844720505L;

		static WrapCopier<ImportModel, Wo> copier = WrapCopierFactory.wo(ImportModel.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
