package com.x.meeting.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.MeetingConfig;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class ActionSaveConfig extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			/* 判断当前用户是否有权限访问 */
			if(!business.isManager(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(BooleanUtils.isTrue(wi.getEnableOnline())){
				if(StringUtils.isBlank(wi.getOnlineProduct())){
					throw new ExceptionCustomError("请配置线上会议产品");
				}
				if(wi.getOnlineProduct().equals(Wi.ONLINE_PROJECT_HST)){
					if(StringUtils.isBlank(wi.getOnlineConfig().getHstUrl())){
						throw new ExceptionCustomError("请配置好视通服务地址");
					}
				}
			}
			MeetingConfig meetingConfig = emc.firstEqual(MeetingConfig.class, MeetingConfig.name_FIELDNAME, MeetingConfig.DEFINITION_MEETING_CONFIG);
			emc.beginTransaction(MeetingConfig.class);
			if(meetingConfig!=null){
				meetingConfig.setProperties(Wi.copier.copy(wi));
			}else{
				meetingConfig = new MeetingConfig();
				meetingConfig.setName(MeetingConfig.DEFINITION_MEETING_CONFIG);
				meetingConfig.setProperties(Wi.copier.copy(wi));
				emc.persist(meetingConfig, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(MeetingConfig.class);
			Wo wo = new Wo();
			wo.setId(meetingConfig.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends MeetingConfigProperties {

		private static final long serialVersionUID = 7830101430627432086L;

		static WrapCopier<Wi, MeetingConfigProperties> copier = WrapCopierFactory.wi(Wi.class, MeetingConfigProperties.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {

	}

}
