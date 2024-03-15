package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;

class ActionListNext extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListNext.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName(), flag, count);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				Co co = (Co) optional.get();
				result.setData(co.getWos());
				result.setCount(co.getCount());
			} else {
				String id = EMPTY_SYMBOL;
				/** 如果不是空位标志位 */
				if (!StringUtils.equals(EMPTY_SYMBOL, flag)) {
					Person o = business.person().pick(flag);
					if (null == o) {
						throw new ExceptionPersonNotExist(flag);
					}
					id = o.getId();
				}

				if (effectivePerson.isSecurityManager()
						|| business.hasAnyRole(effectivePerson, OrganizationDefinition.Manager,
								OrganizationDefinition.OrganizationManager, OrganizationDefinition.PersonManager)) {
					result = this.standardListNext(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, DESC,
							business.personPredicateWithTopUnit(effectivePerson, false));
				} else {
					result = this.standardListNext(Wo.copier2, id, count, JpaObject.sequence_FIELDNAME, DESC,
							business.personPredicateWithTopUnit(effectivePerson, false));
					List<String> list = ListTools.extractField(result.getData(), JpaObject.id_FIELDNAME, String.class,
							true, true);
					List<Wo> wos = Wo.copier.copy(business.person().pick(list));
					result.setData(wos);
					result.setCount((long) wos.size());
				}

				Co co = new Co(result.getData(), result.getCount());
				CacheManager.put(business.cache(), cacheKey, co);
			}
			this.updateControl(effectivePerson, business, result.getData());
			this.hide(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Co extends GsonPropertyObject {

		public Co(List<Wo> wos, Long count) {
			this.wos = wos;
			this.count = count;
		}

		List<Wo> wos;
		Long count;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public List<Wo> getWos() {
			return wos;
		}

		public void setWos(List<Wo> wos) {
			this.wos = wos;
		}
	}

	public static class Wo extends WoPersonAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class,
				JpaObject.singularAttributeField(Person.class, true, true), null);

		static WrapCopier<Person, Wo> copier2 = WrapCopierFactory.wo(Person.class, Wo.class,
				ListTools.toList(JpaObject.id_FIELDNAME), null);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
