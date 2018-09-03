package com.x.program.center.jaxrs.datamappings;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataMappings;

public class ActionGet extends BaseAction {

	public DataMappings execute() throws Exception {
		return Config.dataMappings();
	}
}
