package com.x.processplatform.assemble.surface.jaxrs.serialnumber;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.SerialNumber_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

class ActionListPaging extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionListPaging.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		logger.debug("ActionListPaging execute, page:{}, size:{}.", page, size);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			EntityManager em = emc.get(SerialNumber.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SerialNumber> cq = cb.createQuery(SerialNumber.class);
			Root<SerialNumber> root = cq.from(SerialNumber.class);
			Predicate p = cb.conjunction();
			if(StringUtils.isNotEmpty(wi.getApplicationFlag())){
				Application application = business.application().pick(wi.getApplicationFlag());
				if(application != null) {
					p = cb.and(p, cb.equal(root.get(SerialNumber_.application),wi.getApplicationFlag()));
				}
			}
			if(StringUtils.isNotEmpty(wi.getProcessFlag())){
				Process process = business.process().pick(wi.getProcessFlag());
				if(process!=null) {
					p = cb.and(p, cb.equal(root.get(SerialNumber_.process),wi.getProcessFlag()));
				}
			}
			List<Wo> wos = emc.fetchDescPaging(SerialNumber.class, Wo.copier, p, page, size, JpaObject.sequence_FIELDNAME);
			for (Wo wo : wos) {
				Application application = business.application().pick(wo.getApplication());
				if (application != null){
					wo.setApplicationName(application.getName());
				}
				Process process = business.process().pick(wo.getProcess());
				if(process!=null){
					wo.setProcessName(process.getName());
				}
			}
			result.setData(wos);
			result.setCount(emc.count(SerialNumber.class, p));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -4409718421906673933L;
		@FieldDescribe("应用标志")
		private String applicationFlag;
		@FieldDescribe("流程标志")
		private String processFlag;

		public String getApplicationFlag() {
			return applicationFlag;
		}

		public void setApplicationFlag(String applicationFlag) {
			this.applicationFlag = applicationFlag;
		}

		public String getProcessFlag() {
			return processFlag;
		}

		public void setProcessFlag(String processFlag) {
			this.processFlag = processFlag;
		}
	}

	public static class Wo extends SerialNumber {

		private static final long serialVersionUID = -4409718421906673933L;

		static WrapCopier<SerialNumber, Wo> copier = WrapCopierFactory.wo(SerialNumber.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("应用名称")
		private String applicationName;

		@FieldDescribe("流程名称")
		private String processName;

		public String getApplicationName() {
			return applicationName;
		}

		public void setApplicationName(String applicationName) {
			this.applicationName = applicationName;
		}

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

	}
}
