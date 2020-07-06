package com.x.query.assemble.surface.jaxrs.segment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import org.apache.commons.lang3.BooleanUtils;

class ActionCrawlEnable extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setCms(Config.query().getCrawlCms().getEnable());
			wo.setWork(Config.query().getCrawlWork().getEnable());
			wo.setWorkCompleted(Config.query().getCrawlWorkCompleted().getEnable());
			if (BooleanUtils.isTrue(wo.getCms()) || BooleanUtils.isTrue(wo.getWork())
					|| BooleanUtils.isTrue(wo.getWorkCompleted())) {
				wo.setEnable(true);
			} else {
				wo.setEnable(false);
			}
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private Boolean enable;

		private Boolean cms;

		private Boolean work;

		private Boolean workCompleted;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public Boolean getCms() {
			return cms;
		}

		public void setCms(Boolean cms) {
			this.cms = cms;
		}

		public Boolean getWork() {
			return work;
		}

		public void setWork(Boolean work) {
			this.work = work;
		}

		public Boolean getWorkCompleted() {
			return workCompleted;
		}

		public void setWorkCompleted(Boolean workCompleted) {
			this.workCompleted = workCompleted;
		}

	}
}