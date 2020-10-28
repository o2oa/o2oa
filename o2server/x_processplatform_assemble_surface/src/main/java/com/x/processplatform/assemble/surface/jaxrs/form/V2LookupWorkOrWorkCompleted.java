package com.x.processplatform.assemble.surface.jaxrs.form;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompletedProperties.AdaptForm;
import com.x.processplatform.core.entity.element.Activity;

class V2LookupWorkOrWorkCompleted extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(V2LookupWorkOrWorkCompleted.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (!business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
					new ExceptionEntityNotExist(workOrWorkCompleted))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			CompletableFuture<Wo> _wo = CompletableFuture.supplyAsync(() -> {
				Wo wo = new Wo();
				try {
					Work work = emc.fetch(workOrWorkCompleted, Work.class, ListTools.toList(JpaObject.id_FIELDNAME,
							Work.form_FIELDNAME, Work.activity_FIELDNAME, Work.activityType_FIELDNAME));
					if (null != work) {
						wo = this.work(business, work);
					} else {
						WorkCompleted workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
						if (null != workCompleted) {
							wo = this.workCompleted(business, workCompleted);
						}
					}
				} catch (Exception e) {
					logger.error(e);
				}
				return wo;
			});

			CompletableFuture<Boolean> _control = CompletableFuture.supplyAsync(() -> {
				Boolean value = false;
				try {
					value = business.readableWithWorkOrWorkCompleted(effectivePerson, workOrWorkCompleted,
							new ExceptionEntityNotExist(workOrWorkCompleted));
				} catch (Exception e) {
					logger.error(e);
				}
				return value;
			});

			if (BooleanUtils.isFalse(_control.get())) {
				throw new ExceptionAccessDenied(effectivePerson, workOrWorkCompleted);
			}
			result.setData(_wo.get());
			return result;
		}
	}

	private Wo work(Business business, Work work) throws Exception {
		Wo wo = new Wo();
		if (null != business.form().pick(work.getForm())) {
			wo.setId(work.getForm());
		} else {
			Activity activity = business.getActivity(work);
			wo.setId(PropertyTools.getOrElse(activity, Activity.form_FIELDNAME, String.class, ""));
		}
		return wo;
	}

	private Wo workCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		// 先使用当前库的表单,如果不存在使用储存的表单.
		Wo wo = new Wo();
		if (null != business.form().pick(workCompleted.getForm())) {
			wo.setId(workCompleted.getForm());
		} else if (null != workCompleted.getProperties().getForm()) {
			AdaptForm adapt = workCompleted.getProperties().adaptForm(false);
			wo = XGsonBuilder.convert(adapt, Wo.class);
		}
		return wo;
	}

	public static class Wo extends AbstractWo {

	}

}