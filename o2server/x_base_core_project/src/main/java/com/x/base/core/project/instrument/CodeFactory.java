package com.x.base.core.project.instrument;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.WrapOutBoolean;

public class CodeFactory {

	public void create(String mobile) throws Exception {
		try {
			String url = Config.url_x_program_center_jaxrs("code", "create", "mobile", mobile);
			CipherConnectionAction.get(false, url);
		} catch (Exception e) {
			throw new Exception("CodeFactory create error:" + mobile + ".", e);
		}
	}

	public Boolean validate(String mobile, String answer) throws Exception {
		try {
			String url = Config.url_x_program_center_jaxrs("code", "validate", "mobile", mobile, "answer", answer);
			WrapOutBoolean wrap = CipherConnectionAction.get(false, url).getData(WrapOutBoolean.class);
			return wrap.getValue();
		} catch (Exception e) {
			throw new Exception("CodeFactory validate error:" + mobile + ".", e);
		}
	}

	public Boolean validateCascade(String mobile, String answer) throws Exception {
		try {
			Boolean value = this.validate(mobile, answer);
			if (value == false) {
				String url = Config.url_x_program_center_jaxrs("code", "validate", "mobile", mobile, "answer", answer,
						"cascade");
				WrapOutBoolean wrap = CipherConnectionAction.get(false, url).getData(WrapOutBoolean.class);
				return wrap.getValue();
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new Exception("CodeFactory validateCascade error:" + mobile + ".", e);
		}
	}
}