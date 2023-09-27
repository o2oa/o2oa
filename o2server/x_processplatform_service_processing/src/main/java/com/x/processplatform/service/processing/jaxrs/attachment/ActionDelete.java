package com.x.processplatform.service.processing.jaxrs.attachment;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.ThisApplication;

/**
 * 
 * @author zhour 删除指定附件
 */
class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Attachment attachment = emc.fetch(id, Attachment.class, ListTools.toList(Attachment.job_FIELDNAME));
			if (null == attachment) {
				throw new ExceptionEntityNotExist(id, Attachment.class);
			}
			executorSeed = attachment.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Attachment attachment = emc.find(id, Attachment.class);
					if (null == attachment) {
						throw new ExceptionEntityNotExist(id, Attachment.class);
					}
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							attachment.getStorage());
					// 如果没有存储器,跳过
					if (null != mapping) {
						attachment.deleteContent(mapping);
					}
					emc.beginTransaction(Attachment.class);
					emc.remove(attachment, CheckRemoveType.all);
					emc.commit();
					wo.setId(attachment.getId());
				}
				return "";
			}
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -9052036152229599699L;

	}

}