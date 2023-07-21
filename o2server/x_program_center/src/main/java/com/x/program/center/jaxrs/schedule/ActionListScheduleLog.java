package com.x.program.center.jaxrs.schedule;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.program.center.core.entity.ScheduleLog;

class ActionListScheduleLog extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String application) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<ScheduleLog> list = emc.listEqual(ScheduleLog.class, ScheduleLog.application_FIELDNAME, application);
			List<Wo> wos = Wo.copier.copy(list);
			wos = wos.stream().sorted(Comparator.comparing(Wo::getCreateTime)).collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends ScheduleLog {

		static WrapCopier<ScheduleLog, Wo> copier = WrapCopierFactory.wo(ScheduleLog.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private static final long serialVersionUID = 3325117056239745917L;

	}

}