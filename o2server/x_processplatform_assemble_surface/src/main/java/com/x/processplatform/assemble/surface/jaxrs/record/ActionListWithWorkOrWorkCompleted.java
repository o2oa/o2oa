package com.x.processplatform.assemble.surface.jaxrs.record;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.jaxrs.record.ActionListWithJob.Wo;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.WorkCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithWorkOrWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithWorkOrWorkCompleted.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {

		LOGGER.debug("execute:{}, workOrWorkCompleted:{}.", effectivePerson::getDistinguishedName,
				() -> workOrWorkCompleted);

		ActionResult<List<Wo>> result = new ActionResult<>();
		CompletableFuture<List<Wo>> listFuture = this.listFuture(workOrWorkCompleted);
		CompletableFuture<Boolean> checkControlFuture = this.checkControlFuture(effectivePerson, workOrWorkCompleted);

		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
		}

		result.setData(listFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));

		return result;

	}

	private CompletableFuture<List<Wo>> listFuture(String flag) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> wos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String job = business.job().findWithWork(flag);
				if (null != job) {
					wos = Wo.copier.copy(emc.listEqual(Record.class, Record.job_FIELDNAME, job));
				} else {
					job = business.job().findWithWorkCompleted(flag);
					WorkCompleted workCompleted = emc.firstEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job);
					if (ListTools.isNotEmpty(workCompleted.getRecordList())) {
						wos = Wo.copier.copy(workCompleted.getRecordList());
					} else {
						wos = Wo.copier.copy(emc.listEqual(Record.class, Record.job_FIELDNAME, job));
					}
				}
				wos = wos.stream().sorted(Comparator.comparing(Wo::getOrder)).collect(Collectors.toList());
				for (Task task : emc.listEqual(Task.class, Task.job_FIELDNAME, job).stream()
						.sorted(Comparator.comparing(Task::getStartTime)).collect(Collectors.toList())) {
					wos.add(Wo.copier.copy(this.taskToRecord(task)));
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return wos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Boolean> checkControlFuture(EffectivePerson effectivePerson, String flag) {
		return CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Control control = new JobControlBuilder(effectivePerson, business, flag).enableAllowVisit().build();
				value = control.getAllowVisit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return value;
		}, ThisApplication.forkJoinPool());
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.record.ActionListWithWorkOrWorkCompleted$Wo")
	public static class Wo extends Record {

		private static final long serialVersionUID = -7666329770246726197L;

		static WrapCopier<Record, Wo> copier = WrapCopierFactory.wo(Record.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}