package com.x.base.core.project.instrument;

import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.server.Config;

public class CodeFactory {

	public void create(String mobile) throws Exception {
		try {
			String url = Config.x_program_centerUrlRoot()
					+ Applications.joinQueryUri("code", "create", "mobile", mobile);
			CipherConnectionAction.get(url);
		} catch (Exception e) {
			throw new Exception("CodeFactory create error:" + mobile + ".", e);
		}
	}

	public Boolean validate(String mobile, String answer) throws Exception {
		try {
			String url = Config.x_program_centerUrlRoot()
					+ Applications.joinQueryUri("code", "validate", "mobile", mobile, "answer", answer);
			WrapOutBoolean wrap = CipherConnectionAction.get(url).getData(WrapOutBoolean.class);
			return wrap.getValue();
		} catch (Exception e) {
			throw new Exception("CodeFactory validate error:" + mobile + ".", e);
		}
	}
}