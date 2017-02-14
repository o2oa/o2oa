package com.x.program.center.jaxrs.datamappings;

import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataMappings;

public class ActionGet {

	public DataMappings execute() throws Exception {
		return Config.dataMappings();
	}
}
