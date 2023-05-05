package com.x.meeting.assemble.control.jaxrs.config;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_organization_assemble_personal;
import com.x.meeting.assemble.control.ThisApplication;
import com.x.meeting.core.entity.MeetingConfig;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

class ActionGetConfig extends BaseAction {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetConfig.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo;
			Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass());
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				wo = (Wo) optional.get();
			} else {
				MeetingConfig config = emc.firstEqual(MeetingConfig.class, MeetingConfig.name_FIELDNAME, MeetingConfig.DEFINITION_MEETING_CONFIG);
				if (config != null) {
					wo = Wo.copier.copy(config.getProperties());
				} else {
					MeetingConfigProperties properties = new MeetingConfigProperties();
					ActionResponse response = ThisApplication.context().applications().getQuery(x_organization_assemble_personal.class,
							Applications.joinQueryUri("definition", MeetingConfig.DEFINITION_MEETING_CONFIG));
					String data = response.getData().getAsString();
					if (StringUtils.isNotBlank(data)) {
						properties = gson.fromJson(data, MeetingConfigProperties.class);
					}
					wo = Wo.copier.copy(properties);
				}
				wo.setOnlineConfig(null);
				CacheManager.put(cache, cacheKey, wo);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends MeetingConfigProperties {

		private static final long serialVersionUID = -5752008990171522428L;

		static WrapCopier<MeetingConfigProperties, Wo> copier = WrapCopierFactory.wo(MeetingConfigProperties.class, Wo.class, null,
				JpaObject.FieldsInvisible);


	}
}
