package com.x.processplatform.assemble.surface.jaxrs.sign;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
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
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignStatus;

class ActionListWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithJob.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {

		ActionResult<List<Wo>> result = new ActionResult<>();
		CompletableFuture<List<Wo>> listFuture = listFuture(job);
		CompletableFuture<Boolean> checkControlFuture = checkJobControlFuture(effectivePerson, job);
		result.setData(listFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS));
		if (BooleanUtils
				.isFalse(checkControlFuture.get(Config.processPlatform().getAsynchronousTimeout(), TimeUnit.SECONDS))) {
			throw new ExceptionAccessDenied(effectivePerson, job);
		}
		return result;

	}

	private CompletableFuture<List<Wo>> listFuture(String flag) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> wos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				final String job = flag;
				List<DocSign> docSignList = emc.listEqualAndNotEqual(DocSign.class, DocSign.job_FIELDNAME, job,
						DocSign.status_FIELDNAME, DocSignStatus.STATUS_1.getValue());
				for (DocSign docSign : docSignList) {
					Wo wo = Wo.copier.copy(docSign);
					wo.setInputList(wo.getProperties().getInputList());
					wo.setScrawlList(wo.getProperties().getScrawlList());
					wo.setProperties(null);
					wos.add(wo);
				}
				SortTools.asc(wos, DocSign.createTime_FIELDNAME);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return wos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<Boolean> checkJobControlFuture(EffectivePerson effectivePerson, String job) {
		return CompletableFuture.supplyAsync(() -> {
			Boolean value = false;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Control control = new JobControlBuilder(effectivePerson, business, job).enableAllowVisit().build();
				value = control.getAllowVisit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return value;
		}, ThisApplication.forkJoinPool());
	}

	public static class Wo extends DocSign {

		private static final long serialVersionUID = -3511566791010885542L;

		static WrapCopier<DocSign, Wo> copier = WrapCopierFactory.wo(DocSign.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("输入框列表.")
		private List<String> inputList = new ArrayList<>();
		@FieldDescribe("涂鸦列表.")
		private List<String> scrawlList = new ArrayList<>();

		public List<String> getInputList() {
			return inputList;
		}

		public void setInputList(List<String> inputList) {
			this.inputList = inputList;
		}

		public List<String> getScrawlList() {
			return scrawlList;
		}

		public void setScrawlList(List<String> scrawlList) {
			this.scrawlList = scrawlList;
		}
	}

}
