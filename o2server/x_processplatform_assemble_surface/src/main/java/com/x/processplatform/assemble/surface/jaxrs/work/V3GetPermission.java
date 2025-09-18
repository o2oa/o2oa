package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.Role;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class V3GetPermission extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(V3GetPermission.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workOrWorkCompleted) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> workOrWorkCompleted);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Work work = null;
		WorkCompleted workCompleted = null;
		String applicationId = "";
		String processId = "";
		String job = "";
		List<String> names = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Control control = new JobControlBuilder(effectivePerson, business, workOrWorkCompleted).enableAllowVisit()
					.build();
			if (BooleanUtils.isTrue(control.getAllowVisit())) {
				wo.setEnable(true);
				work = emc.find(workOrWorkCompleted, Work.class);
				if (null == work) {
					workCompleted = emc.flag(workOrWorkCompleted, WorkCompleted.class);
				}
				List<Review> reviews = new ArrayList<>();
				if (null != work) {
					applicationId = work.getApplication();
					processId = work.getProcess();
					job = work.getJob();
				} else if (null != workCompleted) {
					applicationId = workCompleted.getApplication();
					processId = workCompleted.getProcess();
					job = workCompleted.getJob();
				}
				Application application = business.application().pick(applicationId);
				names.addAll(application.getControllerList());
				Process process = business.process().pick(processId);
				names.addAll(process.getControllerList());
				reviews = emc.listEqual(Review.class, Review.job_FIELDNAME, job);
				names.addAll(reviews.stream().map(Review::getPerson).collect(Collectors.toList()));
				List<Role> roles = business.organization().role().listObject(
						List.of(OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager));
				roles.stream().forEach(o -> {
					names.addAll(o.getGroupList());
					names.addAll(o.getPersonList());
				});
				wo.setAccessibleList(names.stream().distinct().collect(Collectors.toList()));
			} else {
				wo.setEnable(false);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -8740908895201213169L;

		@FieldDescribe("是否有权限访问")
		private Boolean enable = false;

		@FieldDescribe("可访问标识")
		private List<String> accessibleList = new ArrayList<>();

		private List<String> managableList = new ArrayList<>();

		public List<String> getAccessibleList() {
			return accessibleList;
		}

		public void setAccessibleList(List<String> accessibleList) {
			this.accessibleList = accessibleList;
		}

		public List<String> getManagableList() {
			return managableList;
		}

		public void setManagableList(List<String> managableList) {
			this.managableList = managableList;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

	}

}
