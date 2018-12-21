package com.x.organization.assemble.authentication.jaxrs.bind;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.SortTools;
import com.x.organization.assemble.authentication.wrapout.WrapOutBind;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Bind_;

class ActionList extends BaseAction {
	ActionResult<List<WrapOutBind>> execute() throws Exception {
		ActionResult<List<WrapOutBind>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<WrapOutBind> wraps = outCopier.copy(emc.list(Bind.class, this.list(emc)));
			SortTools.asc(wraps, "createTime");
			result.setData(wraps);
			return result;
		}
	}

	private List<String> list(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Bind.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Bind> root = cq.from(Bind.class);
		cq.select(root.get(Bind_.id));
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}
}
