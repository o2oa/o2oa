package com.x.program.center.jaxrs.invoke;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.Crypto;

class ActionToken extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		String value = wi.getPerson() + "#" + wi.getDate().getTime();
		wo.setValue(Crypto.encrypt(value, wi.getKey()));
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -251331390296713913L;

		@FieldDescribe("用户标识")
		private String person;

		@FieldDescribe("时间,如果为空那么采用当前时间.")
		private Date date;

		public Date getDate() {
			return (this.date == null) ? new Date() : this.date;
		}

		@FieldDescribe("密钥")
		private String key;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

	public static class Wo extends WrapString {

	}

}
