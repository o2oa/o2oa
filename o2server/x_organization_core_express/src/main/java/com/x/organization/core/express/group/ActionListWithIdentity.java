package com.x.organization.core.express.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListWithIdentity extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection,
									   boolean recursiveGroupFlag, boolean referenceFlag, boolean recursiveOrgFlag) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getIdentityList().addAll(collection);
			wi.setRecursiveGroupFlag(recursiveGroupFlag);
			wi.setReferenceFlag(referenceFlag);
			wi.setRecursiveOrgFlag(recursiveOrgFlag);
		}
		Wo wo = context.applications().postQuery(applicationClass, "group/list/identity", wi).getData(Wo.class);
		return wo.getGroupList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("个人")
		private List<String> identityList = new ArrayList<>();

		@FieldDescribe("是否递归查询上级群组，默认true")
		private Boolean recursiveGroupFlag = true;

		@FieldDescribe("是否包含查找人员身份成员、人员归属组织成员的所属群组，默认false")
		private Boolean referenceFlag = false;

		@FieldDescribe("是否递归人员归属组织的上级组织所属群组，前提referenceFlag为true，默认false")
		private Boolean recursiveOrgFlag = false;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public Boolean getReferenceFlag() {
			return referenceFlag;
		}

		public void setReferenceFlag(Boolean referenceFlag) {
			this.referenceFlag = referenceFlag;
		}

		public Boolean getRecursiveGroupFlag() {
			return recursiveGroupFlag;
		}

		public void setRecursiveGroupFlag(Boolean recursiveGroupFlag) {
			this.recursiveGroupFlag = recursiveGroupFlag;
		}

		public Boolean getRecursiveOrgFlag() {
			return recursiveOrgFlag;
		}

		public void setRecursiveOrgFlag(Boolean recursiveOrgFlag) {
			this.recursiveOrgFlag = recursiveOrgFlag;
		}
	}

	public static class Wo extends WoGroupAbstract {
	}
}
