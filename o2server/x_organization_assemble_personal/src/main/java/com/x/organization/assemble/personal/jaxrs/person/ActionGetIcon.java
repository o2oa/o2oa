package com.x.organization.assemble.personal.jaxrs.person;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionGetIcon extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, effectivePerson);
				CacheManager.put(business.cache(), cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	private Wo get(Business business, EffectivePerson effectivePerson) throws Exception {
		String base64;
		if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
			/* 如果是xadmin单独处理 */
			base64 = com.x.base.core.project.config.Person.ICON_MANAGER;
		} else {
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null == person) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			base64 = person.getIcon();
			if (StringUtils.isEmpty(base64)) {
				if (Objects.equals(GenderType.m, person.getGenderType())) {
					base64 = com.x.base.core.project.config.Person.ICON_MALE;
				} else if (Objects.equals(GenderType.f, person.getGenderType())) {
					base64 = com.x.base.core.project.config.Person.ICON_FEMALE;
				} else {
					base64 = com.x.base.core.project.config.Person.ICON_UNKOWN;
				}
			}
		}
		byte[] bs = Base64.decodeBase64(base64);
		Wo wo = new Wo(bs, this.contentType(false, "icon.png"), this.contentDisposition(false, "icon.png"));
		return wo;
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

}
