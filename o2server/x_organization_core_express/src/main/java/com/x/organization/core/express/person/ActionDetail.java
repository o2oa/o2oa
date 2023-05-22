package com.x.organization.core.express.person;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.PersonDetail;

class ActionDetail extends BaseAction {

	public static PersonDetail execute(AbstractContext context, String flag, Boolean fectchIdentity, Boolean fectchUnit,
			Boolean fectchUnitDuty, Boolean fectchGroup, Boolean fectchRole, Boolean fectchPersonAttribute)
			throws Exception {
		Wi wi = new Wi();
		wi.setFetchIdentity(BooleanUtils.isNotFalse(fectchIdentity));
		wi.setFetchUnit(BooleanUtils.isNotFalse(fectchUnit));
		wi.setFetchUnitDuty(BooleanUtils.isNotFalse(fectchUnitDuty));
		wi.setFetchGroup(BooleanUtils.isNotFalse(fectchGroup));
		wi.setFetchRole(BooleanUtils.isNotFalse(fectchRole));
		wi.setFetchPersonAttribute(BooleanUtils.isNotFalse(fectchPersonAttribute));
		return context.applications()
				.postQuery(applicationClass, Applications.joinQueryUri("person", "detail", flag), wi)
				.getData(PersonDetail.class);
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -3725925544463863946L;
		@FieldDescribe("是否获取身份.")
		private Boolean fetchIdentity;
		@FieldDescribe("是否获取组织.")
		private Boolean fetchUnit;
		@FieldDescribe("是否获取组织职务.")
		private Boolean fetchUnitDuty;
		@FieldDescribe("是否获取群组.")
		private Boolean fetchGroup;
		@FieldDescribe("是否获取角色.")
		private Boolean fetchRole;
		@FieldDescribe("是否获取个人属性.")
		private Boolean fetchPersonAttribute;

		public Boolean getFetchIdentity() {
			return fetchIdentity;
		}

		public void setFetchIdentity(Boolean fetchIdentity) {
			this.fetchIdentity = fetchIdentity;
		}

		public Boolean getFetchUnit() {
			return fetchUnit;
		}

		public void setFetchUnit(Boolean fetchUnit) {
			this.fetchUnit = fetchUnit;
		}

		public Boolean getFetchUnitDuty() {
			return fetchUnitDuty;
		}

		public void setFetchUnitDuty(Boolean fetchUnitDuty) {
			this.fetchUnitDuty = fetchUnitDuty;
		}

		public Boolean getFetchGroup() {
			return fetchGroup;
		}

		public void setFetchGroup(Boolean fetchGroup) {
			this.fetchGroup = fetchGroup;
		}

		public Boolean getFetchRole() {
			return fetchRole;
		}

		public void setFetchRole(Boolean fetchRole) {
			this.fetchRole = fetchRole;
		}

		public Boolean getFetchPersonAttribute() {
			return fetchPersonAttribute;
		}

		public void setFetchPersonAttribute(Boolean fetchPersonAttribute) {
			this.fetchPersonAttribute = fetchPersonAttribute;
		}

	}

	public static class Wo extends PersonDetail {

		private static final long serialVersionUID = 8539563151592249288L;

	}
}
