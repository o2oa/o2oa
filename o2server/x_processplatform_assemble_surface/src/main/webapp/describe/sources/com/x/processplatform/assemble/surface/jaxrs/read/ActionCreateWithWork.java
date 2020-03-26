package com.x.processplatform.assemble.surface.jaxrs.read;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControl;
import com.x.processplatform.core.entity.content.Work;

class ActionCreateWithWork extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String workId, JsonElement jsonElement)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Work work = null;
		if (ListTools.isEmpty(wi.getIdentityList())) {
			throw new ExceptionEmptyIdentity();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			work = emc.find(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			if (effectivePerson.isNotManager()) {
				WoWorkControl workControl = business.getControl(effectivePerson, work, WoWorkControl.class);
				if (!workControl.getAllowVisit()) {
					throw new ExceptionAccessDenied(effectivePerson, work);
				}
			}
		}

		List<Wo> wos = ThisApplication.context().applications()
				.postQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
						"read/work/" + URLEncoder.encode(work.getId(), DefaultCharset.name), wi, work.getJob())
				.getDataAsList(Wo.class);
		result.setData(wos);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("待阅标识")
		private List<String> identityList = new ArrayList<>();

		@FieldDescribe("发送待阅通知")
		private Boolean notify = false;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public Boolean getNotify() {
			return notify;
		}

		public void setNotify(Boolean notify) {
			this.notify = notify;
		}

	}

	public static class WoWorkControl extends WorkControl {

	}

	public static class Wo extends WoId {
	}

}
