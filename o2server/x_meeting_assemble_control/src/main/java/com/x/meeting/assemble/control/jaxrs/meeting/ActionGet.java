package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.PropertyTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Meeting;
import com.x.meeting.core.entity.MeetingConfigProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Meeting meeting = emc.find(id, Meeting.class, ExceptionWhen.not_found);
			Wo wo = Wo.copier.copy(meeting);
			MeetingConfigProperties config = business.getConfig();
			if(StringUtils.isNotBlank(wo.getRoomLink()) && config.onLineEnabled()){
				if(BooleanUtils.isTrue(config.getOnlineConfig().getHstAuth())) {
					Person person = business.organization().person().getObject(effectivePerson.getDistinguishedName());
					String userId = PropertyTools.getOrElse(person, config.getOnlineConfig().getO2ToHstUid(), String.class, person.getUnique());
					userId = StringUtils.isNoneBlank(userId) ? userId : person.getUnique();
					wo.setRoomLink(wo.getRoomLink()+"&userName=" + userId + "&userPwd="+ URLEncoder.encode(generateHstPwd(userId), DefaultCharset.charset));
				}else{
					wo.setRoomLink(wo.getRoomLink() + "&userName=" + effectivePerson.getName());
				}
			}
			WrapTools.setAttachment(business, wo);
			WrapTools.decorate(business, wo, effectivePerson);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapOutMeeting {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
