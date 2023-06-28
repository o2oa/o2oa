package com.x.processplatform.assemble.surface.jaxrs.record;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithJobPaging extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJobPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job, Integer page, Integer size)
			throws Exception {

		LOGGER.debug("execute:{}, job:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> job,
				() -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			Business business = new Business(emc);

			String workOrWorkCompleted = "";
			List<String> works = business.work().listWithJob(job);
			if (ListTools.isNotEmpty(works)) {
				workOrWorkCompleted = works.get(0);
			} else {
				works = business.workCompleted().listWithJob(job);
				if (ListTools.isNotEmpty(works)) {
					workOrWorkCompleted = works.get(0);
				} else {
					throw new ExceptionEntityNotExist(job);
				}
			}

			Control control = new JobControlBuilder(effectivePerson, business, workOrWorkCompleted).enableAllowVisit()
					.build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
			}

			List<Wo> wos = emc.fetchEqualAscPaging(Record.class, Wo.copier, Record.job_FIELDNAME, job, page, size,
					Record.order_FIELDNAME);

			for (Task task : emc.listEqual(Task.class, Task.job_FIELDNAME, job).stream()
					.sorted(Comparator.comparing(Task::getStartTime)).collect(Collectors.toList())) {
				Record rec = this.taskToRecord(task);
				wos.add(Wo.copier.copy(rec));
			}

			result.setData(wos);
			return result;
		}

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.record.ActionListWithJobPaging$Wo")
	public static class Wo extends Record {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class,
				JpaObject.singularAttributeField(Record.class, true, false), JpaObject.FieldsInvisible);

	}

}