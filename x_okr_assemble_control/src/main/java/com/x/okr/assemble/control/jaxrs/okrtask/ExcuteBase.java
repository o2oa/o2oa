package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.WrapOutOkrWorkReportBaseInfo;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteBase {
	
	protected BeanCopyTools<OkrTask, WrapOutOkrTask> wrapout_copier = BeanCopyToolsBuilder.create( OkrTask.class, WrapOutOkrTask.class, null, WrapOutOkrTask.Excludes);
	protected BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> okrWorkReportBaseInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
	protected BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> okrWorkBaseInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
	protected OkrTaskService okrTaskService = new OkrTaskService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	protected OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	
}
