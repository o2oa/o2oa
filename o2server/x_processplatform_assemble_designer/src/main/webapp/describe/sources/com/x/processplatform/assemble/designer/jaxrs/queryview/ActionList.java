package com.x.processplatform.assemble.designer.jaxrs.queryview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.QueryView;

class ActionList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			List<QueryView> os = this.list(business);
			List<Application> apps = this.listApplication(business, os);
			if (ListTools.isNotEmpty(os) && ListTools.isNotEmpty(apps)) {
				os = os.stream()
						.sorted(Comparator.comparing(QueryView::getApplication).thenComparing(QueryView::getName))
						.collect(Collectors.toList());
				apps = apps.stream().sorted(Comparator.comparing(Application::getName)).collect(Collectors.toList());
				for (Application o : apps) {
					wos.add(Wo.copier.copy(o));
				}
				loop: for (QueryView o : os) {
					for (Wo wo : wos) {
						if (StringUtils.equals(wo.getId(), o.getApplication())) {
							wo.getQueryViewList().add(WoQueryView.copier.copy(o));
							continue loop;
						}
					}
				}
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -5809308465773293836L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("视图")
		private List<WoQueryView> queryViewList = new ArrayList<>();

		public List<WoQueryView> getQueryViewList() {
			return queryViewList;
		}

		public void setQueryViewList(List<WoQueryView> queryViewList) {
			this.queryViewList = queryViewList;
		}
	}

	public static class WoQueryView extends QueryView {

		private static final long serialVersionUID = 7569137298897210037L;

		static WrapCopier<QueryView, WoQueryView> copier = WrapCopierFactory.wo(QueryView.class, WoQueryView.class,
				null, ListTools.toList(JpaObject.FieldsInvisible, QueryView.updateTime_FIELDNAME));

	}

	private List<QueryView> list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueryView> cq = cb.createQuery(QueryView.class);
		Root<QueryView> root = cq.from(QueryView.class);
		List<QueryView> list = em.createQuery(cq.select(root)).getResultList();
		return list;
	}

	private List<Application> listApplication(Business business, List<QueryView> os) throws Exception {
		List<String> ids = ListTools.extractProperty(os, "application", String.class, true, true);
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Application> cq = cb.createQuery(Application.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = root.get(Application_.id).in(ids);
		List<Application> list = em.createQuery(cq.select(root).where(p)).getResultList();
		return list;
	}
}
