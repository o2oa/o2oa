package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Meeting;

class ActionAppliedCompleted extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithAppliedCompleted(effectivePerson.getDistinguishedName());
			List<Wo> wos = Wo.copier.copy(emc.list(Meeting.class, ids));
			WrapTools.decorate(business, wos, effectivePerson);
			WrapTools.setAttachment(business, wos);
			SortTools.desc(wos, Meeting.startTime_FIELDNAME);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WrapOutMeeting {
		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
