package com.x.organization.assemble.express.jaxrs.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;

import net.sf.ehcache.Element;

class ActionListWithGroup extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getGroupList(), ","));
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((Wo) element.getObjectValue());
			} else {
				Wo wo = this.list(business, wi);
				cache.put(new Element(cacheKey, wo));
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("群组")
		private List<String> groupList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

	}

	public static class Wo extends WoIdentityAbstract {

	}

	private Wo list(Business business, Wi wi) throws Exception {

		List<Group> list = new ArrayList<>();
		List<Group> os = business.group().pick(wi.getGroupList());
		for (Group o : os) {
			list.add(o);
			list.addAll(business.group().listSubNestedObject(o));
		}
		List<String> personIds = new ArrayList<>();
		List<String> unitIds = new ArrayList<>();
		for (Group o : list) {
			personIds.addAll(o.getPersonList());
			if (ListTools.isNotEmpty(o.getUnitList())) {
				unitIds.addAll(o.getUnitList());
			}
		}
		/* 先解析组织 */
		List<Identity> identities = new ArrayList<>();
		unitIds = ListTools.trim(unitIds, true, true);
		if (ListTools.isNotEmpty(unitIds)) {
			identities.addAll(
					business.entityManagerContainer().fetchIn(Identity.class, Identity.unit_FIELDNAME, unitIds));
		}

		/* 再把人全部解析出来,同时判断是否已经在组织中已经出现了 */
		personIds = ListTools.trim(personIds, true, true);
		List<String> existPersonIds = ListTools.extractProperty(identities, Identity.person_FIELDNAME, String.class,
				true, true);
		for (Identity o : business.identity().listMajorOfPerson(business, personIds)) {
			if (!existPersonIds.contains(o.getPerson())) {
				identities.add(o);
			}
		}
		identities = ListTools.trim(identities, true, true);
		Wo wo = new Wo();
		wo.getIdentityList().addAll(ListTools.extractProperty(business.identity().sort(identities),
				JpaObject.DISTINGUISHEDNAME, String.class, true, true));
		return wo;
	}

}