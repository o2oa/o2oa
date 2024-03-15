package com.x.processplatform.service.processing.jaxrs.attachment;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionEditText extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEditText.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Attachment attachment = null;
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attachment = emc.fetch(id, Attachment.class, ListTools.toList(Attachment.job_FIELDNAME));
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			executorSeed = attachment.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				ActionResult<Wo> result = new ActionResult<>();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Attachment attachment = emc.find(id, Attachment.class);
					if (null == attachment) {
						throw new ExceptionEntityNotExist(id, Attachment.class);
					}
					emc.beginTransaction(Attachment.class);
					Wi.copier.copy(wi, attachment);
					emc.check(attachment, CheckPersistType.all);
					emc.commit();
					Wo wo = new Wo();
					wo.setId(attachment.getId());
					result.setData(wo);
					return result;
				}
			}
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wi extends Attachment {

		private static final long serialVersionUID = 4243967432624425952L;

		static WrapCopier<Wi, Attachment> copier = WrapCopierFactory.wi(Wi.class, Attachment.class,
				Arrays.asList(Attachment.text_FIELDNAME), null);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 166373158402012448L;

	}

}