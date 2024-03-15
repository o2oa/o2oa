package com.x.organization.assemble.express.jaxrs.distinguishedname;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.express.Business;

class ActionList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionList.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), StringUtils.join(wi.getDistinguishedNameList(), ","));
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = valid(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo valid(Business business, Wi wi) throws Exception {
		List<String> list = new ArrayList<>();
		if (null != wi.getDistinguishedNameList()) {
			for (String name : wi.getDistinguishedNameList()) {
				if (pick(business, name)) {
					list.add(name);
				}
			}
		}
		Wo wo = new Wo();
		wo.setDistinguishedNameList(list);
		return wo;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -2496038304285557158L;

		@FieldDescribe("专有名称列表.")
		private List<String> distinguishedNameList;

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7419421356146588398L;

		@FieldDescribe("专有名称列表.")
		private List<String> distinguishedNameList;

		public List<String> getDistinguishedNameList() {
			return distinguishedNameList;
		}

		public void setDistinguishedNameList(List<String> distinguishedNameList) {
			this.distinguishedNameList = distinguishedNameList;
		}
	}

}