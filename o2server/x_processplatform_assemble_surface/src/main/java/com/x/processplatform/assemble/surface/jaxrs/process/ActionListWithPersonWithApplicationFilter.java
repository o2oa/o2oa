package com.x.processplatform.assemble.surface.jaxrs.process;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import com.x.processplatform.core.express.service.processing.jaxrs.process.ActionListWithPersonWithApplicationFilterWi;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ActionListWithPersonWithApplicationFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithPersonWithApplicationFilter.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName, () -> applicationFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			List<String> groups = business.organization().group().listWithIdentity(identities);
			List<String> ids = list(business, effectivePerson, identities, units, groups, application, wi);
			for (String id : ids) {
				wos.add(Wo.copier.copy(business.process().pick(id)));
			}
			wos = business.process().sort(wos);
			result.setData(wos);
			return result;
		}
	}

	public List<String> list(Business business, EffectivePerson effectivePerson, List<String> identities,
			List<String> units, List<String> groups, Application application, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (effectivePerson.isNotManager()
				&& (!BooleanUtils.isTrue(business.organization().person().hasRole(effectivePerson,
						OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager)))) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableUnitList)),
					cb.isEmpty(root.get(Process_.startableGroupList)));
			p = cb.or(p, cb.equal(root.get(Process_.creatorPerson), effectivePerson.getDistinguishedName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Process_.startableUnitList).in(units));
			}
			if (ListTools.isNotEmpty(groups)) {
				p = cb.or(p, root.get(Process_.startableGroupList).in(groups));
			}
		}
		p = cb.and(p, cb.equal(root.get(Process_.application), application.getId()));
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		if (StringUtils.equals(wi.getStartableTerminal(), Process.STARTABLETERMINAL_CLIENT)) {
			p = cb.and(p,
					cb.or(cb.isNull(root.get(Process_.startableTerminal)),
							cb.equal(root.get(Process_.startableTerminal), ""),
							cb.equal(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_CLIENT),
							cb.equal(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_ALL)));
		} else if (StringUtils.equals(wi.getStartableTerminal(), Process.STARTABLETERMINAL_MOBILE)) {
			p = cb.and(p,
					cb.or(cb.isNull(root.get(Process_.startableTerminal)),
							cb.equal(root.get(Process_.startableTerminal), ""),
							cb.equal(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_MOBILE),
							cb.equal(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_ALL)));
		}

		cq.select(root.get(Process_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListWithPersonWithApplicationFilter$Wo")
	public static class Wo extends Process {

		private static final long serialVersionUID = 1521228691441978462L;

		static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListWithPersonWithApplicationFilter$Wi")
	public static class Wi extends ActionListWithPersonWithApplicationFilterWi {

		private static final long serialVersionUID = -4479972681926734549L;

	}
}
