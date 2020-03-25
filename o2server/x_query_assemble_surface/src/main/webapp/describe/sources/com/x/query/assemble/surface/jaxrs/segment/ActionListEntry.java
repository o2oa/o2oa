package com.x.query.assemble.surface.jaxrs.segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.segment.Entry;

class ActionListEntry extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			if (ListTools.isNotEmpty(wi.getEntryList())) {
				List<Entry> os = emc.list(Entry.class, true, wi.getEntryList());
				wos = Wo.copier.copy(os);
			}
			this.visible(business, wos);
			result.setData(wos);
			return result;
		}
	}

	private void visible(Business business, List<Wo> wos) throws Exception {

		List<String> workEntries = new ArrayList<>();
		List<String> workCompletedEntries = new ArrayList<>();
		List<String> cmsEntries = new ArrayList<>();

		for (Wo wo : wos) {
			if (Objects.equals(Entry.TYPE_WORK, wo.getType())) {
				workEntries.add(wo.getBundle());
			} else if (Objects.equals(Entry.TYPE_WORKCOMPLETED, wo.getType())) {
				workCompletedEntries.add(wo.getBundle());
			} else if (Objects.equals(Entry.TYPE_CMS, wo.getType())) {
				cmsEntries.add(wo.getBundle());
			}
		}

		if (ListTools.isNotEmpty(workEntries)) {
			List<String> list = this.visible_work(business, workEntries);
			for (Wo wo : wos) {
				if (ListTools.contains(list, wo.getBundle())) {
					wo.setVisible(true);
				}
			}
		}

		if (ListTools.isNotEmpty(workCompletedEntries)) {
			List<String> list = this.visible_workCompleted(business, workEntries);
			for (Wo wo : wos) {
				if (ListTools.contains(list, wo.getBundle())) {
					wo.setVisible(true);
				}
			}
		}

		if (ListTools.isNotEmpty(cmsEntries)) {
			List<String> list = this.visible_cms(business, workEntries);
			for (Wo wo : wos) {
				if (ListTools.contains(list, wo.getBundle())) {
					wo.setVisible(true);
				}
			}
		}

	}

	private List<String> visible_work(Business business, List<String> bundles) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		cq.select(root.get(Review_.job)).where(cb.isMember(root.get(Review_.job), cb.literal(bundles)));
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	private List<String> visible_workCompleted(Business business, List<String> bundles) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		cq.select(root.get(Review_.job)).where(cb.isMember(root.get(Review_.job), cb.literal(bundles)));
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	private List<String> visible_cms(Business business, List<String> bundles) throws Exception {
		EntityManager em = business.entityManagerContainer().get(com.x.cms.core.entity.Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<com.x.cms.core.entity.Review> root = cq.from(com.x.cms.core.entity.Review.class);
		cq.select(root.get(com.x.cms.core.entity.Review_.docId))
				.where(cb.isMember(root.get(com.x.cms.core.entity.Review_.docId), cb.literal(bundles)));
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	public static class Wo extends Entry {

		private static final long serialVersionUID = -8067704098385000667L;

		static WrapCopier<Entry, Wo> copier = WrapCopierFactory.wo(Entry.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("是否可见.")
		private Boolean visible = false;

		public Boolean getVisible() {
			return visible;
		}

		public void setVisible(Boolean visible) {
			this.visible = visible;
		}

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("数据")
		private List<String> entryList = new ArrayList<>();

		public List<String> getEntryList() {
			return entryList;
		}

		public void setEntryList(List<String> entryList) {
			this.entryList = entryList;
		}

	}
}