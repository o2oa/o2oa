package com.x.cms.assemble.control.jaxrs.log;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.cms.core.entity.Log;

public class ExcuteBase {
	
	BeanCopyTools<Log, WrapOutLog> copier = BeanCopyToolsBuilder.create(Log.class, WrapOutLog.class, null, WrapOutLog.Excludes);
	
}
