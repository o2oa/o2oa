package com.x.cms.assemble.control.jaxrs.queryview;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.element.QueryView_;

public class ActionList extends BaseAction {

	public ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		List<String> identities = null;
		List<String> unitNames = null;
		
		identities = userManagerService.listIdentitiesWithPerson( effectivePerson.getDistinguishedName() );
		unitNames = userManagerService.listUnitNamesWithPerson( effectivePerson.getDistinguishedName() );
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wraps = new ArrayList<>();
			List<String> ids = this.list( business, effectivePerson, identities, unitNames );
			List<QueryView> os = business.entityManagerContainer().list(QueryView.class, ids);
			wraps = Wo.copier.copy(os);
			SortTools.asc( wraps, true, "name" );
			result.setData(wraps);
			return result;
		}
	}

	private List<String> list( Business business, EffectivePerson effectivePerson, List<String> identities, List<String> unitNames ) throws Exception {
		EntityManager em = business.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.conjunction();
		/* 不是管理员或者流程管理员 */
		if ( effectivePerson.isNotManager() && userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(), OrganizationDefinition.ProcessPlatformManager )) {
			p = cb.equal(root.get(QueryView_.creatorPerson), effectivePerson.getDistinguishedName());
			p = cb.or(p, root.get(QueryView_.controllerList).in(effectivePerson.getDistinguishedName()));
			p = cb.or(p,
					cb.and(cb.isEmpty(root.get(QueryView_.availablePersonList)),
							cb.isEmpty(root.get(QueryView_.availableUnitList)),
							cb.isEmpty(root.get(QueryView_.availableIdentityList))));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(QueryView_.availablePersonList)));
			p = cb.or(p, root.get(QueryView_.availableUnitList).in(unitNames));
			p = cb.or(p, root.get(QueryView_.availableIdentityList).in(identities));
		}
		cq.select(root.get(QueryView_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;
		
		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		public static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo( QueryView.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}