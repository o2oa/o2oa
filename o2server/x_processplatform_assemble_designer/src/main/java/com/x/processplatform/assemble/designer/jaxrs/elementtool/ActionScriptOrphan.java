package com.x.processplatform.assemble.designer.jaxrs.elementtool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Script_;

class ActionScriptOrphan extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> applicationIds = emc.ids(Application.class);
			Wo wo = new Wo();
			wo.setScriptList(emc.fetch(this.listOrphanScript(business, applicationIds), WoScript.copier));
			result.setData(wo);
			return result;
		}
	}

	private List<String> listOrphanScript(Business business, List<String> applicationIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Script.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Script> root = cq.from(Script.class);
		Predicate p = cb.not(root.get(Script_.application).in(applicationIds));
		cq.select(root.get(Script_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public static class WoScript extends Script {

		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				ListTools.toList(JpaObject.id_FIELDNAME, Script.name_FIELDNAME, Script.alias_FIELDNAME,
						Script.application_FIELDNAME),
				null);
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoScript> scriptList = new ArrayList<>();

		public List<WoScript> getScriptList() {
			return scriptList;
		}

		public void setScriptList(List<WoScript> scriptList) {
			this.scriptList = scriptList;
		}

	}
}