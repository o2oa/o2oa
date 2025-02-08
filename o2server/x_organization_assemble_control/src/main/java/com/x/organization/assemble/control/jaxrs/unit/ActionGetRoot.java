package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.LcInfo;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.Unit;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

class ActionGetRoot extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetRoot.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass());
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business);
				CacheManager.put(business.cache(), cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wo extends WoAbstractUnit {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接下级身份数量")
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

	private Wo get(Business business) throws Exception{
		Wo wo = null;
		try {
			Class<?> licenseToolsCls = Class.forName("com.x.base.core.lc.LcTools");
			String info = (String) MethodUtils.invokeStaticMethod(licenseToolsCls, "getInfo");
			if(StringUtils.isNotBlank(info)){
				LcInfo lc = XGsonBuilder.instance().fromJson(info, LcInfo.class);
				wo = new Wo();
				wo.setName(StringUtils.defaultIfEmpty(lc.getUnitName(), lc.getName()));
				wo.setUnique("$root");
				wo.setDistinguishedName(wo.getName() + PersistenceProperties.distinguishNameSplit + wo.getUnique()
						+ PersistenceProperties.distinguishNameSplit + PersistenceProperties.Unit.distinguishNameCharacter);
				wo.setLevel(0);
				wo.setOrderNumber(0);
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		if(wo != null) {
			wo.setSubDirectIdentityCount(0L);
			wo.setSubDirectUnitCount(business.entityManagerContainer()
					.countEqual(Unit.class, Unit.level_FIELDNAME, 1));
		}
		return wo;
	}

}
