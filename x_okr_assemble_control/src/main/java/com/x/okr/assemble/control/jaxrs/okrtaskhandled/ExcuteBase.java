package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.control.service.OkrTaskHandledService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrTaskHandled;

public class ExcuteBase {

	protected BeanCopyTools<OkrTaskHandled, WrapOutOkrTaskHandled> wrapout_copier = BeanCopyToolsBuilder.create( OkrTaskHandled.class, WrapOutOkrTaskHandled.class, null, WrapOutOkrTaskHandled.Excludes);
	protected OkrTaskHandledService okrTaskHandledService = new OkrTaskHandledService();
	protected OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	
}
