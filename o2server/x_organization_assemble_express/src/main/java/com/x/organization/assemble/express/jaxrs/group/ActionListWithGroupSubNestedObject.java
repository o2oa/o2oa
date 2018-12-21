package com.x.organization.assemble.express.jaxrs.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;

import net.sf.ehcache.Element;

class ActionListWithGroupSubNestedObject extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.join(wi.getGroupList(), ","));
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business, wi);
				cache.put(new Element(cacheKey, wos));
				result.setData(wos);
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

	public static class Wo extends com.x.base.core.project.organization.Group {

	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		List<Group> os = business.group().pick(wi.getGroupList());
		List<String> groupIds = new ArrayList<>();
		for (Group o : os) {
			groupIds.addAll(business.group().listSubNested(o.getId()));
		}
		groupIds = ListTools.trim(groupIds, true, true);
		List<Group> list = business.group().pick(groupIds);
		list = business.group().sort(list);
		for (Group o : list) {
			wos.add(this.convert(business, o, Wo.class));
		}
		return wos;
	}

}