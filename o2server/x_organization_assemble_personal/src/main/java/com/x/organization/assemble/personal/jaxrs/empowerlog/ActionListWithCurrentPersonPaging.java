package com.x.organization.assemble.personal.jaxrs.empowerlog;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.core.entity.accredit.EmpowerLog;
import com.x.organization.core.entity.accredit.EmpowerLog_;

class ActionListWithCurrentPersonPaging extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(EmpowerLog.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EmpowerLog> root = cq.from(EmpowerLog.class);
			Predicate p = cb.equal(root.get(EmpowerLog_.fromPerson), effectivePerson.getDistinguishedName());
			List<Wo> wos = emc.fetchDescPaging(EmpowerLog.class, Wo.copier, p, page, size,
					EmpowerLog.createTime_FIELDNAME);
			result.setData(wos);
			result.setCount(emc.count(EmpowerLog.class, p));
			return result;
		}
	}

	public static class Wo extends EmpowerLog {

		private static final long serialVersionUID = 4279205128463146835L;

		static WrapCopier<EmpowerLog, Wo> copier = WrapCopierFactory.wi(EmpowerLog.class, Wo.class, null,
				JpaObject.FieldsUnmodify);

	}

}
