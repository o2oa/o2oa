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
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictItem_;
import com.x.processplatform.core.entity.element.ApplicationDict_;

class ActionApplicationDictOrphan extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> applicationIds = emc.ids(Application.class);
			List<String> applicationDictIds = emc.ids(ApplicationDict.class);
			Wo wo = new Wo();
			wo.setApplicationDictList(
					emc.fetch(this.listOrphanApplicationDict(business, applicationIds), WoApplicationDict.copier));
			wo.setApplicationDictItemList(emc.fetch(this.listOrphanApplicationDictItem(business, applicationDictIds),
					WoApplicationDictItem.copier));
			result.setData(wo);
			return result;
		}
	}

	private List<String> listOrphanApplicationDict(Business business, List<String> applicationIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDict> root = cq.from(ApplicationDict.class);
		Predicate p = cb.not(root.get(ApplicationDict_.application).in(applicationIds));
		cq.select(root.get(ApplicationDict_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private List<String> listOrphanApplicationDictItem(Business business, List<String> applicationDictIds)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.not(root.get(ApplicationDictItem_.bundle).in(applicationDictIds));
		cq.select(root.get(ApplicationDictItem_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public static class WoApplicationDict extends ApplicationDict {

		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<ApplicationDict, WoApplicationDict> copier = WrapCopierFactory.wo(ApplicationDict.class,
				WoApplicationDict.class, ListTools.toList(JpaObject.id_FIELDNAME, ApplicationDict.name_FIELDNAME,
						ApplicationDict.alias_FIELDNAME, ApplicationDict.application_FIELDNAME),
				null);
	}

	public static class WoApplicationDictItem extends ApplicationDictItem {
		private static final long serialVersionUID = -5257306329734318116L;
		static WrapCopier<ApplicationDictItem, WoApplicationDictItem> copier = WrapCopierFactory.wo(
				ApplicationDictItem.class, WoApplicationDictItem.class,
				ListTools.toList(JpaObject.id_FIELDNAME, ApplicationDictItem.bundle_FIELDNAME), null);
	}

	public static class Wo extends GsonPropertyObject {

		private List<WoApplicationDict> applicationDictList = new ArrayList<>();
		private List<WoApplicationDictItem> applicationDictItemList = new ArrayList<>();

		public List<WoApplicationDict> getApplicationDictList() {
			return applicationDictList;
		}

		public void setApplicationDictList(List<WoApplicationDict> applicationDictList) {
			this.applicationDictList = applicationDictList;
		}

		public List<WoApplicationDictItem> getApplicationDictItemList() {
			return applicationDictItemList;
		}

		public void setApplicationDictItemList(List<WoApplicationDictItem> applicationDictItemList) {
			this.applicationDictItemList = applicationDictItemList;
		}

	}
}