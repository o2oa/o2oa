package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapOutOkrWorkReportPersonLink;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.WrapOutOkrWorkReportProcessLog;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportFlowService;
import com.x.okr.assemble.control.service.OkrWorkReportOperationService;
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ExcuteBase {
	protected BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
	protected BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
	protected BeanCopyTools<OkrWorkReportProcessLog, WrapOutOkrWorkReportProcessLog> okrWorkReportProcessLog_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportProcessLog.class, WrapOutOkrWorkReportProcessLog.class, null, WrapOutOkrWorkReportProcessLog.Excludes);
	protected OkrWorkReportFlowService okrWorkReportFlowService = new OkrWorkReportFlowService();
	protected OkrWorkReportOperationService okrWorkReportOperationService = new OkrWorkReportOperationService();
	protected OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
	protected OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	protected OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
	protected OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	protected OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	protected OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	protected OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	protected OkrTaskService okrTaskService = new OkrTaskService();
	protected OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	protected OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	protected OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	protected DateOperation dateOperation = new DateOperation();

}
