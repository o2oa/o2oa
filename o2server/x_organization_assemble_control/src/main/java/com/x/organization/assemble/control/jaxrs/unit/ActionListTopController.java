package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import java.util.List;
import java.util.Optional;

class ActionListTopController extends BaseAction {
	/** 用于可管理的顶层组织,输出下级组织和组织成员数量 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos;
				if(business.isOrgManager(effectivePerson)) {
					wos = this.list(business);
				} else {
					wos = this.listController(business, effectivePerson);
				}
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wo extends WoAbstractUnit {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接成员身份数量")
		private Long subDirectIdentityCount = 0L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public Long getSubDirectUnitCount() {
			return subDirectUnitCount;
		}

		public void setSubDirectUnitCount(Long subDirectUnitCount) {
			this.subDirectUnitCount = subDirectUnitCount;
		}

		public Long getSubDirectIdentityCount() {
			return subDirectIdentityCount;
		}

		public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
			this.subDirectIdentityCount = subDirectIdentityCount;
		}

	}

	private List<Wo> list(Business business) throws Exception {
		List<Unit> os = business.unit().listTopObject();
		List<Wo> wos = Wo.copier.copy(os);
		wos.stream().forEach(o -> {
			try {
				o.setSubDirectUnitCount(
						business.entityManagerContainer().countEqual(Unit.class, Unit.superior_FIELDNAME, o.getId()));
				o.setSubDirectIdentityCount(business.entityManagerContainer().countEqual(Identity.class,
						Identity.unit_FIELDNAME, o.getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		wos = business.unit().sort(wos);
		return wos;
	}

	private List<Wo> listController(Business business, EffectivePerson effectivePerson) throws Exception {
		Person person = business.person().pick(effectivePerson.getDistinguishedName());
		List<Unit> os = business.unit().listControlUnitWithPerson(person.getId());
		List<Wo> wos = business.unit().sort(Wo.copier.copy(os));
		if(wos.size() > 1){
			while (!wos.get(0).getLevel().equals(wos.get(wos.size()-1).getLevel())){
				wos.remove(wos.size()-1);
			}
		}
		wos.forEach(o -> {
			try {
				o.setSubDirectUnitCount(
						business.entityManagerContainer().countEqual(Unit.class, Unit.superior_FIELDNAME, o.getId()));
				o.setSubDirectIdentityCount(business.entityManagerContainer().countEqual(Identity.class,
						Identity.unit_FIELDNAME, o.getId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return wos;
	}

}
