package com.x.base.core.project.instrument;

import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.Applications;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.jaxrs.CaptchaWo;
import com.x.base.core.project.server.Config;

public class CaptchaFactory {

	public CaptchaWo create() throws Exception {
		return this.create(120, 50);
	}

	public CaptchaWo create(Integer width, Integer height) throws Exception {
		try {
			String url = Config.x_program_centerUrlRoot() + Applications.joinQueryUri("captcha", "create", "width",
					width.toString(), "height", height.toString());
			CaptchaWo o = CipherConnectionAction.get(url).getData(CaptchaWo.class);
			return o;
		} catch (Exception e) {
			throw new Exception("CaptchaFactory create error.", e);
		}
	}

	public Boolean validate(String id, String answer) throws Exception {
		try {
			String url = Config.x_program_centerUrlRoot()
					+ Applications.joinQueryUri("captcha", id, "validate", "answer", answer);
			WrapOutBoolean wrap = CipherConnectionAction.get(url).getData(WrapOutBoolean.class);
			return wrap.getValue();
		} catch (Exception e) {
			throw new Exception("CaptchaFactory validate error.", e);
		}
	}

}
