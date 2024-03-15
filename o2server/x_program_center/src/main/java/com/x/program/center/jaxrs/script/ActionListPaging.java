package com.x.program.center.jaxrs.script;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

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
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.core.entity.Script;

class ActionListPaging extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			if(effectivePerson.isAnonymous()) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			EntityManager em = emc.get(Script.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			Predicate p = cb.conjunction();
			List<Wo> wos = emc.fetchDescPaging(Script.class, Wo.copier, p, page, size, Script.sequence_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(Script.class, p));
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -4613524095238698445L;
		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Script.dependScriptList_FIELDNAME, Script.text_FIELDNAME));

	}
}
