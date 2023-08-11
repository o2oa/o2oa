package com.x.processplatform.assemble.surface.jaxrs.datarecord;

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
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.DataRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class ActionListWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJob.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {
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

	private CompletableFuture<List<Wo>> listFuture(String flag) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> wos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				wos = emc.fetchEqual(DataRecord.class, Wo.copier, DataRecord.job_FIELDNAME, flag);
				SortTools.desc(wos, DataRecord.updateTime_FIELDNAME);
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

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.datarecord.ActionListWithJob$Wo")
	public static class Wo extends DataRecord {

		private static final long serialVersionUID = -3887850760955249124L;

		static WrapCopier<DataRecord, Wo> copier = WrapCopierFactory.wo(DataRecord.class, Wo.class,
				singularAttributeField(DataRecord.class, true, true),
				null);
	}

}
