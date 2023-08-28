package com.x.meeting.assemble.control.jaxrs.meeting;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.assemble.control.WrapTools;
import com.x.meeting.assemble.control.wrapout.WrapOutMeeting;
import com.x.meeting.core.entity.Meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class ActionListOnDayAll extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer year, Integer month, Integer day)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date start = calendar.getTime();
			/** 一天内 */
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MILLISECOND, -1);
			Date end = calendar.getTime();
			List<String> ids = new ArrayList<>();
			ids = business.meeting().listWithDate(start, end);
			List<Wo> wos = Wo.copier.copy(emc.list(Meeting.class, ids));
			this.setOnlineLink(business, effectivePerson, wos);
			WrapTools.decorate(business, wos, effectivePerson);
			WrapTools.setAttachment(business, wos);
			SortTools.asc(wos, false, "startTime");
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WrapOutMeeting {

		private static final long serialVersionUID = 4609263020989488356L;
		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null, Wo.Excludes);

	}

}
