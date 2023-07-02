package com.x.program.center.jaxrs.storagemappings;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMappings;

public class ActionGet extends BaseAction {

	public StorageMappings execute() throws Exception {
		return Config.storageMappings();
	}
}