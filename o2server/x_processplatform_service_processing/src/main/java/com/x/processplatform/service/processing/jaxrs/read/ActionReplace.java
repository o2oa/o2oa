package com.x.processplatform.service.processing.jaxrs.read;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.*;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class ActionReplace extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isBlank(wi.getTargetIdentity())){
			throw new ExceptionFieldEmpty(Handover.targetIdentity_FIELDNAME);
		}
        String executorSeed = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String person = business.organization().person().getWithIdentity(wi.getTargetIdentity());
			if(StringUtils.isBlank(person)){
				throw new ExceptionPersonNotExist(wi.getTargetIdentity());
			}
			wi.setTargetPerson(person);
            Read read = emc.fetch(id, Read.class, ListTools.toList(Read.job_FIELDNAME, Read.person_FIELDNAME));

            if (null == read) {
                throw new ExceptionEntityNotExist(id, Read.class);
            }
            if(!read.getPerson().equals(wi.getPerson())){
            	throw new ExceptionAccessDenied(wi.getPerson());
			}

            executorSeed = read.getJob();
        }

        return ProcessPlatformKeyClassifyExecutorFactory.get(executorSeed).submit(new CallableImpl(id, wi)).get(300,
                TimeUnit.SECONDS);
    }

    private class CallableImpl implements Callable<ActionResult<Wo>> {

        private String id;
        private Wi wi;

        private CallableImpl(String id, Wi wi) {
            this.id = id;
            this.wi = wi;
        }

        @Override
        public ActionResult<Wo> call() throws Exception {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Wo wo = new Wo();
				wo.setValue(true);
				Read read = emc.find(id, Read.class);
				emc.beginTransaction(Read.class);
				emc.beginTransaction(ReadCompleted.class);
				emc.beginTransaction(Review.class);
				List<ReadCompleted> readCompletedList = emc.listEqualAndEqual(ReadCompleted.class, ReadCompleted.person_FIELDNAME,
						read.getPerson(), ReadCompleted.job_FIELDNAME, read.getJob());
				readCompletedList.stream().forEach(o -> {
					o.setPerson(wi.getTargetPerson());
					o.setIdentity(wi.getTargetIdentity());
				});
				List<Review> reviewList = emc.listEqualAndEqual(Review.class, Review.person_FIELDNAME, read.getPerson(),
						Review.job_FIELDNAME, read.getJob());
				reviewList.stream().forEach(o -> o.setPerson(wi.getTargetPerson()));

				read.setPerson(wi.getTargetPerson());
				read.setIdentity(wi.getTargetIdentity());
				emc.commit();

                ActionResult<Wo> result = new ActionResult<>();
                result.setData(wo);
                return result;
            }
        }
    }

    public static class Wi extends Handover {

		private static final long serialVersionUID = -6215838156429443320L;

		static WrapCopier<Wi, Handover> copier = WrapCopierFactory.wi(Wi.class, Handover.class,
                ListTools.toList(Handover.person_FIELDNAME, Handover.targetIdentity_FIELDNAME), null);

    }

    public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -8577678018996847686L;
	}

}
