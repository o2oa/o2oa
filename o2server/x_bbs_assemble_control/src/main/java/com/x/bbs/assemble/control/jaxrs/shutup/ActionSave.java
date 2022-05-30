package com.x.bbs.assemble.control.jaxrs.shutup;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSShutup;
import org.apache.commons.lang3.StringUtils;

class ActionSave extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if(!business.isManager(effectivePerson)){
				throw new ExceptionAccessDenied(effectivePerson);
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(StringUtils.isBlank(wi.getPerson())){
				throw new ExceptionFieldEmpty(BBSShutup.person_FIELDNAME);
			}
			if(StringUtils.isBlank(wi.getUnmuteDate())){
				throw new ExceptionFieldEmpty(BBSShutup.unmuteDate_FIELDNAME);
			}
			if(StringUtils.isBlank(wi.getReason())){
				throw new ExceptionFieldEmpty(BBSShutup.reason_FIELDNAME);
			}
			BBSShutup shutup = Wi.copier.copy(wi);
			if(StringUtils.isNotBlank(wi.getId())){
				shutup = emc.find(wi.getId(), BBSShutup.class);
				if(shutup == null){
					throw new ExceptionEntityNotExist(wi.getId());
				}
				wi.copyTo(shutup);
			}
			emc.beginTransaction(BBSShutup.class);
			shutup.setUnmuteDateTime(DateTools.parse(shutup.getUnmuteDate()));
			shutup.setOperator(effectivePerson.getDistinguishedName());
			if(StringUtils.isBlank(wi.getId())) {
				emc.persist(shutup, CheckPersistType.all);
			}else{
				emc.check(shutup, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(shutup.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		static WrapCopier<Wi, BBSShutup> copier = WrapCopierFactory.wi(Wi.class, BBSShutup.class, null,
				ListTools.toList(JpaObject.FieldsUnmodifyExcludeId, BBSShutup.unmuteDateTime_FIELDNAME, BBSShutup.operator_FIELDNAME));

		@FieldDescribe("数据库主键.")
		private String id;

		@FieldDescribe("被禁言用户")
		private String person;

		@FieldDescribe("解封时间")
		private String unmuteDate;

		@FieldDescribe("禁言原因")
		private String reason;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getUnmuteDate() {
			return unmuteDate;
		}

		public void setUnmuteDate(String unmuteDate) {
			this.unmuteDate = unmuteDate;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}
	}

	public static class Wo extends WoId {

	}

}
