package com.x.processplatform.assemble.surface.jaxrs.queryview;

import java.util.ArrayList;
import java.util.List;

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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.element.QueryView_;

class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExecute.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationFlag);
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wraps = new ArrayList<>();
			List<String> ids = this.list(business, effectivePerson, application);
			List<QueryView> os = business.entityManagerContainer().list(QueryView.class, ids);
			wraps = Wo.copier.copy(os);
			SortTools.asc(wraps, true, "name");
			result.setData(wraps);
			return result;
		}
	}

	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo(QueryView.class, Wo.class, null, Wo.Excludes);

	}

	private List<String> list(Business business, EffectivePerson effectivePerson, Application application)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.conjunction();
		/* 不是管理员或者流程管理员 */
		if (effectivePerson.isNotManager()
				&& (!business.organization().person().hasRole(effectivePerson,
						OrganizationDefinition.ProcessPlatformManager, OrganizationDefinition.Manager))
				&& effectivePerson.isNotPerson(application.getControllerList())) {
			p = cb.equal(root.get(QueryView_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.or(p, root.get(QueryView_.controllerList).in(effectivePerson.getDistinguishedName()));
			p = cb.or(p,
					cb.and(cb.isEmpty(root.get(QueryView_.availablePersonList)),
							cb.isEmpty(root.get(QueryView_.availableUnitList)),
							cb.isEmpty(root.get(QueryView_.availableIdentityList))));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(QueryView_.availablePersonList)));
			p = cb.or(p, root.get(QueryView_.availableUnitList)
					.in(business.organization().unit().listWithPersonSupNested(effectivePerson)));
			p = cb.or(p, root.get(QueryView_.availableIdentityList)
					.in(business.organization().identity().listWithPerson(effectivePerson.getDistinguishedName())));
		}
		p = cb.and(p, cb.equal(root.get(QueryView_.application), application.getId()));
		p = cb.and(p, cb.notEqual(root.get(QueryView_.display), false));
		cq.select(root.get(QueryView_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

}