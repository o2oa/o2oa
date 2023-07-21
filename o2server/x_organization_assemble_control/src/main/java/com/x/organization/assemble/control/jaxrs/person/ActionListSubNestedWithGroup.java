package com.x.organization.assemble.control.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;

class ActionListSubNestedWithGroup extends BaseAction {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListSubNestedWithGroup.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String groupFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), groupFlag);
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Wo> wos = this.list(business, groupFlag);
				CacheManager.put(business.cache(), cacheKey, wos);
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			this.hide(effectivePerson, business, result.getData());
			return result;
		}
	}

	private List<Wo> list(Business business, String groupFlag) throws Exception {
		Group group = business.group().pick(groupFlag);
		if (null == group) {
			throw new ExceptionGroupNotExist(groupFlag);
		}
		List<String> ids = new ArrayList<>();
		ids.addAll(business.group().listSubNested(group.getId()));
		/* 将当前群组也加入到需要搜索成员的群组中 */
		ids.add(group.getId());
		ArrayList<String> personIds = new ArrayList<>();
		ArrayList<String> unitIds = new ArrayList<>();
		for (String str : ids) {
			Group o = business.group().pick(str);
			if (null != o) {
				personIds.addAll(o.getPersonList());
				for (String unitId : o.getUnitList()) {
					unitIds.add(unitId);
					unitIds.addAll(business.unit().listSupNested(unitId));
				}
			}
			unitIds = ListTools.trim(unitIds, true, true);
			if (ListTools.isNotEmpty(unitIds)) {
				List<Identity> identities = business.entityManagerContainer().listIn(Identity.class,
						Identity.unit_FIELDNAME, unitIds);
				if (ListTools.isNotEmpty(identities)) {
					personIds.addAll(
							ListTools.extractProperty(identities, Identity.person_FIELDNAME, String.class, true, true));
				}
			}
			personIds = ListTools.trim(personIds, true, true);
		}
		List<Wo> wos = Wo.copier.copy(business.person().pick(personIds));
		wos = business.person().sort(wos);
		// 产生头像
		return wos;
	}

	public static class Wo extends WoPersonAbstract {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null,
				person_fieldsInvisible);
	}

}
