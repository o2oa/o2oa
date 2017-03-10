package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.control.service.OkrConfigWorkLevelService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrConfigWorkLevel;

public class ExcuteBase {
	
	protected BeanCopyTools<OkrConfigWorkLevel, WrapOutOkrConfigWorkLevel> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigWorkLevel.class, WrapOutOkrConfigWorkLevel.class, null, WrapOutOkrConfigWorkLevel.Excludes);
	protected OkrConfigWorkLevelService okrConfigWorkLevelService = new OkrConfigWorkLevelService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	
}
