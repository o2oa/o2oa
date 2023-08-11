package com.x.processplatform.assemble.surface.jaxrs.readrecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJob.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			CompletableFuture<List<Wo>> listFuture = this.listFuture(job);
			CompletableFuture<Boolean> checkControlFuture = this.checkControlFuture(effectivePerson, job);

			if (BooleanUtils.isFalse(
					checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}

			result.setData(listFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
			return result;
		}

	}

	private CompletableFuture<List<Wo>> listFuture(String flag) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> wos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<Read> readList = emc.listEqual(Read.class, Read.job_FIELDNAME, flag);
				List<ReadCompleted> readCompletedList = emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME,
						flag);
				readList = readList.stream().sorted(Comparator.comparing(Read::getStartTime))
						.collect(Collectors.toList());
				readCompletedList = readCompletedList.stream().sorted(Comparator.comparing(ReadCompleted::getStartTime))
						.collect(Collectors.toList());
				for (ReadCompleted readCompleted : readCompletedList) {
					Wo wo = new Wo();
					readCompleted.copyTo(wo, true, "type");
					wo.setType(ReadRecord.TYPE_READ_COMPLETED);
					wos.add(wo);
				}
				for (Read read : readList) {
					Wo wo = new Wo();
					read.copyTo(wo, true, "type");
					wo.setType(ReadRecord.TYPE_READ);
					wos.add(wo);
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readrecord.ActionListWithJob$Wo")
	public static class Wo extends ReadRecord {

		private static final long serialVersionUID = 6825855035865296913L;

	}

}
