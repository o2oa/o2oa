package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.V2AddManualTaskIdentityMatrixWi;
import com.x.processplatform.service.processing.Business;

class V2AddManualTaskIdentityMatrix extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V2AddManualTaskIdentityMatrix.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		}

		Work work = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = getWork(business, id);
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
		}

		return ProcessPlatformExecutorFactory.get(work.getJob())
				.submit(new CallableImpl(work.getId(), wi.getIdentity(), wi.getOptionList(), wi.getRemove()))
				.get(300, TimeUnit.SECONDS);

	}

	private Work getWork(Business business, String id) throws Exception {
		return business.entityManagerContainer().fetch(id, Work.class, Arrays.asList(Work.job_FIELDNAME));
	}

	private class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;
		private String identity;
		private List<V2AddManualTaskIdentityMatrixWi.Option> optionList;
		private Boolean remove;

		CallableImpl(String id, String identity, List<V2AddManualTaskIdentityMatrixWi.Option> optionList, Boolean remove) {
			this.id = id;
			this.identity = identity;
			this.optionList = optionList;
			this.remove = remove;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				emc.beginTransaction(Work.class);
				Work work = emc.find(this.id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				ManualTaskIdentityMatrix matrix = work.getManualTaskIdentityMatrix();
				for (V2AddManualTaskIdentityMatrixWi.Option option : optionList) {
					List<String> identities = business.organization().identity().list(option.getIdentityList());
					if (!ListTools.isEmpty(identities)) {
						matrix.add(id, option.getPosition(), identities);
					}
				}
				if (remove) {
					matrix.remove(identity);
				}
				work.setManualTaskIdentityMatrix(matrix);
				emc.check(work, CheckPersistType.all);
				emc.commit();
			} catch (Exception e) {
				LOGGER.error(e);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}

	}

	public static class Wi extends V2AddManualTaskIdentityMatrixWi {

		private static final long serialVersionUID = 7870902860170655791L;

	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -8882214104176786739L;

	}

}