package com.x.processplatform.service.processing.jaxrs.read;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionProcessing extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionProcessing.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Read read = emc.fetch(id, Read.class, ListTools.toList(Read.job_FIELDNAME));
			if (null == read) {
				throw new ExceptionEntityNotExist(id, Read.class);
			}
			executorSeed = read.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					//Business business = new Business(emc);
					Read read = emc.find(id, Read.class);
					if (null == read) {
						throw new ExceptionEntityNotExist(id, Read.class);
					}
					emc.beginTransaction(Read.class);
					emc.beginTransaction(ReadCompleted.class);
					Date now = new Date();
					Long duration = Config.workTime().betweenMinutes(read.getStartTime(), now);
					ReadCompleted readCompleted = new ReadCompleted(read, now, duration);
//					List<ReadCompleted> exists = listExist(business, read);
//					if (exists.isEmpty()) {
//						emc.persist(readCompleted, CheckPersistType.all);
//						MessageFactory.readCompleted_create(readCompleted);
//					} else {
//						for (ReadCompleted o : exists) {
//							if (StringUtils.isEmpty(readCompleted.getOpinion())) {
//								readCompleted.copyTo(o,
//										ListTools.toList(JpaObject.FieldsUnmodify, ReadCompleted.opinion_FIELDNAME));
//							} else {
//								readCompleted.copyTo(o, ListTools.toList(JpaObject.FieldsUnmodify));
//							}
//						}
//					}
					emc.persist(readCompleted, CheckPersistType.all);
					MessageFactory.readCompleted_create(readCompleted);
					emc.remove(read, CheckRemoveType.all);
					emc.commit();
					MessageFactory.read_to_readCompleted(readCompleted);
					wo.setId(read.getId());
				}
				return "";
			}
		};

		ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 8647539327144668439L;
	}

	private List<ReadCompleted> listExist(Business business, Read read) throws Exception {
		return business.entityManagerContainer().listEqualAndEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME,
				read.getJob(), ReadCompleted.person_FIELDNAME, read.getPerson());
	}

}
