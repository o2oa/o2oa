package com.x.base.core.project.instrument;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;

public class CollectFactory {

	public void person() throws Exception {
		try {
			String url = Config.x_program_centerUrlRoot() + Applications.joinQueryUri("collect", "person");
			CipherConnectionAction.get(false, url);
		} catch (Exception e) {
			throw new Exception("CollectFactory create error.", e);
		}
	}

}