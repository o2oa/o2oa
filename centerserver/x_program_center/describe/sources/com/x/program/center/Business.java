package com.x.program.center;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.factory.PersonFactory;
import com.x.program.center.factory.UnitFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	public Boolean collectAccountNotEmpty() throws Exception {
		if (StringUtils.isEmpty(Config.collect().getName()) || StringUtils.isEmpty(Config.collect().getPassword())) {
			return false;
		}
		return true;
	}

	public Boolean connectCollect() {
		try {
			String url = Config.collect().url("o2_collect_assemble/jaxrs/echo");
			ConnectionAction.get(url, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Boolean validateCollect() throws Exception {
		String url = Config.collect().url("o2_collect_assemble/jaxrs/collect/validate");
		ValidateReq req = new ValidateReq();
		req.setName(Config.collect().getName());
		req.setPassword(Config.collect().getPassword());
		ActionResponse resp = ConnectionAction.put(url, null, req);
		return resp.getData(WoValidateCollect.class).getValue();
	}

	public static class ValidateReq extends GsonPropertyObject {

		private String name;
		private String password;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory(this);
		}
		return person;
	}

	private UnitFactory unit;

	public UnitFactory unit() throws Exception {
		if (null == this.unit) {
			this.unit = new UnitFactory(this);
		}
		return unit;
	}

	public static class WoValidateCollect extends WrapBoolean {
	}

}
