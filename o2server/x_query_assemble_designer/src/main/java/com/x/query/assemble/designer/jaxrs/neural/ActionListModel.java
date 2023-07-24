package com.x.query.assemble.designer.jaxrs.neural;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.neural.Model;

class ActionListModel extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListModel.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if ((!effectivePerson.isManager()) && (!business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<Model> os = emc.listAll(Model.class);
			result.setData(Wo.copier.copy(os));
			return result;
		}
	}

	public static class Wo extends Model {

		private static final long serialVersionUID = -6541538280679110474L;

		static WrapCopier<Model, Wo> copier = WrapCopierFactory.wo(Model.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Model.nnet_FIELDNAME, Model.intermediateNnet_FIELDNAME));
	}

}