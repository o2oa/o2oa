package com.x.organization.assemble.express.jaxrs.person;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Person;

class ActionGetNickName extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), flag);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = new Wo();
				Person person = emc.find(flag, Person.class);
				if(person==null){
					person = business.person().pick(flag);
				}
				if(person==null) {
					wo.setValue(flag);
				}else{
					if(StringUtils.isNoneBlank(person.getNickName())){
						wo.setValue(person.getNickName());
					}else{
						wo.setValue(person.getName());
					}
				}
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WrapString {

	}

}
