package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionReportProcessLogList;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportDetailQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportListByWorkId;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportProcessLog;

public class ActionListByWork extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListByWork.class );
	
	protected ActionResult<List<WoOkrWorkReportBaseInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String workId ) throws Exception {
		ActionResult<List<WoOkrWorkReportBaseInfo>> result = new ActionResult<List<WoOkrWorkReportBaseInfo>>();
		OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
		OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
		OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
		List<WoOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		List<String> ids = null;
		List<String> logsIds = null;

		if ( workId == null || workId.isEmpty() ) {
			Exception exception = new ExceptionWorkIdEmpty();
			result.error(exception);
		} else {
			try {
				ids = okrWorkReportQueryService.listByWorkId(workId);
				if (ids != null && !ids.isEmpty()) {
					okrWorkReportBaseInfoList = okrWorkReportQueryService.listByIds(ids);
					if (okrWorkReportBaseInfoList != null && !okrWorkReportBaseInfoList.isEmpty()) {
						wraps = WoOkrWorkReportBaseInfo.copier.copy(okrWorkReportBaseInfoList);
						for (WoOkrWorkReportBaseInfo wrap : wraps) {
							try {
								logsIds = okrWorkReportProcessLogService.listByReportId(wrap.getId());
								if (logsIds != null) {
									okrWorkReportProcessLogList = okrWorkReportProcessLogService.list(logsIds);
									if ( okrWorkReportProcessLogList != null ) {
										wrap.setProcessLogs( WoOkrWorkReportProcessLog.copier.copy(okrWorkReportProcessLogList));
									}
								}
							} catch (Exception e) {
								Exception exception = new ExceptionReportProcessLogList(e, wrap.getId());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
							}
							try {
								okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get(wrap.getId());
								if (okrWorkReportDetailInfo != null) {
									wrap.setWorkPlan(okrWorkReportDetailInfo.getWorkPlan());
									wrap.setWorkPointAndRequirements(
											okrWorkReportDetailInfo.getWorkPointAndRequirements());
									wrap.setProgressDescription(okrWorkReportDetailInfo.getProgressDescription());
								}
							} catch (Exception e) {
								Exception exception = new ExceptionWorkReportDetailQueryById(e, wrap.getId());
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
							}
							String workDetail = okrWorkDetailInfoService.getWorkDetailWithId(wrap.getWorkId());
							if (workDetail != null && !workDetail.isEmpty()) {
								wrap.setTitle(workDetail);
							}
						}
						SortTools.asc(wraps, "reportCount");
						result.setData(wraps);
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkReportListByWorkId(e, workId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
}