package com.x.custom.index.assemble.control.jaxrs.reveal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.custom.index.assemble.control.Business;
import com.x.custom.index.core.entity.Reveal;
import com.x.custom.index.core.entity.Reveal_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListEditable extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListEditable.class);

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
		Optional<?> optional = CacheManager.get(revealCacheCategory, cacheKey);
		if (optional.isPresent()) {
			wos = ((List<Wo>) optional.get());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<Reveal> list = list(business, effectivePerson);
				wos = Wo.copier.copy(list);
				CacheManager.put(revealCacheCategory, cacheKey, wos);
			}
		}
		result.setData(wos);
		return result;
	}

	public List<Reveal> list(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Reveal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reveal> cq = cb.createQuery(Reveal.class);
		Root<Reveal> root = cq.from(Reveal.class);
		Predicate p = cb.conjunction();
		if ((!effectivePerson.isManager())
				&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager))) {
			p = cb.equal(root.get(Reveal_.creatorPerson), effectivePerson.getDistinguishedName());
		}
		cq.select(root).where(p);
		List<Reveal> list = em.createQuery(cq).getResultList();
		return list.stream()
				.sorted(Comparator.comparing(Reveal::getOrderNumber, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionListEditable$Wo")
	public static class Wo extends Reveal {

		private static final long serialVersionUID = -7226320999297317820L;

		static WrapCopier<Reveal, Wo> copier = WrapCopierFactory.wo(Reveal.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}