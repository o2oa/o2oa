package com.x.query.assemble.surface.jaxrs.statement;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ActionListWithQuery extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String queryFlag, Boolean justSelect, Boolean hasView) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Query query = emc.flag(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag);
			}
			if (!business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Wo> wos = new ArrayList<>();
			for (String id : emc.idsEqual(Statement.class, Statement.query_FIELDNAME, query.getId())) {
				Statement o = business.pick(id, Statement.class);
				if (null != o) {
					if(BooleanUtils.isTrue(hasView) && StringUtils.isBlank(o.getView())){
						continue;
					}
					if(BooleanUtils.isTrue(justSelect) && !Statement.TYPE_SELECT.equals(o.getType())){
						continue;
					}
					if (business.readable(effectivePerson, o)) {
						wos.add(Wo.copier.copy(o));
					}
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Statement {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Statement, Wo> copier = WrapCopierFactory.wo(Statement.class, Wo.class,
				JpaObject.singularAttributeField(Statement.class, true, true), JpaObject.FieldsInvisible);
	}
}
