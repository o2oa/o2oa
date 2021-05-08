package com.x.base.core.project.instrument;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.WoCaptcha;

public class CaptchaFactory {

	public WoCaptcha create() throws Exception {
		return this.create(120, 50);
	}

	public WoCaptcha create(Integer width, Integer height) throws Exception {
		try {
			String url = Config.url_x_program_center_jaxrs("captcha", "v2", "create", "width", width.toString(),
					"height", height.toString());
			Wo o = CipherConnectionAction.get(false, url).getData(Wo.class);
			return o;
		} catch (Exception e) {
			throw new Exception("CaptchaFactory create error.", e);
		}
	}

	public static class Wo extends WoCaptcha {

	}

	public Boolean validate(String id, String answer) throws Exception {
		try {
			String url = Config.url_x_program_center_jaxrs("captcha", id, "validate", "answer", answer);
			WrapOutBoolean wrap = CipherConnectionAction.get(false, url).getData(WrapOutBoolean.class);
			return wrap.getValue();
		} catch (Exception e) {
			throw new Exception("CaptchaFactory validate error.", e);
		}
	}

}
