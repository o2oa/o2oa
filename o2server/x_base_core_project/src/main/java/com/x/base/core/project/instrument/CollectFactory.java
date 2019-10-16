package com.x.base.core.project.instrument;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;

public class CollectFactory {

	public void person() throws Exception {
		try {
			String url = Config.url_x_program_center_jaxrs("collect", "person");
			CipherConnectionAction.get(false, url);
		} catch (Exception e) {
			throw new Exception("CollectFactory create error.", e);
		}
	}

}