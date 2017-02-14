package com.x.program.center.jaxrs.storagemappings;

import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.StorageMappings;

public class ActionGet {

	public StorageMappings execute() throws Exception {
		return Config.storageMappings();
	}
}