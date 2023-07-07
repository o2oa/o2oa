package com.x.processplatform.assemble.surface.jaxrs.review;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkCompletedControlBuilder;
import com.x.processplatform.core.entity.content.WorkCompleted;

/**
 * 为指定的用户根据WorkCompleted直接创建WorkCompleted
 * 
 * @author zhour
 *
 */
class ActionCreateWithWorkCompleted extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithWorkCompleted.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> jsonElement);
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		WorkCompleted workCompleted = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			workCompleted = emc.find(wi.getWorkCompleted(), WorkCompleted.class);
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(wi.getWorkCompleted(), WorkCompleted.class);
			}
			Control control = new WorkCompletedControlBuilder(effectivePerson, business, workCompleted)
					.enableAllowVisit().build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit())) {
				throw new ExceptionAccessDenied(effectivePerson, workCompleted);
			}
			List<String> people = business.organization().person().list(wi.getPersonList());
			if (ListTools.isEmpty(people)) {
				throw new ExceptionPersonEmpty();
			}
		}
		List<Wo> wos = ThisApplication.context().applications()
				.postQuery(x_processplatform_service_processing.class,
						Applications.joinQueryUri("review", "create", "workcompleted"), wi, workCompleted.getJob())
				.getDataAsList(Wo.class);
		result.setData(wos);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 6222459992132813639L;

		@FieldDescribe("已完成工作")
		private String workCompleted;

		@FieldDescribe("可阅读人员")
		private List<String> personList = new ArrayList<>();

		public String getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(String workCompleted) {
			this.workCompleted = workCompleted;
		}

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2975931994110720014L;

	}

}
