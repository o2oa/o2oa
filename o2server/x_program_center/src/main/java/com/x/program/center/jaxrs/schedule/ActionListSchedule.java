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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.Schedule;

class ActionListSchedule extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListSchedule.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Schedule> list = emc.listAll(Schedule.class);
			List<Wo> wos = Wo.copier.copy(list);
			wos = wos.stream().sorted(Comparator.comparing(Wo::getApplication).thenComparing(Wo::getNode))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Schedule {

		static WrapCopier<Schedule, Wo> copier = WrapCopierFactory.wo(Schedule.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private static final long serialVersionUID = 3325117056239745917L;

	}
}
