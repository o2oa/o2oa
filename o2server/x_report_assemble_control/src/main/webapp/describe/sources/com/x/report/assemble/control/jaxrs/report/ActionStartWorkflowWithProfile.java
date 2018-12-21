package com.x.report.assemble.control.jaxrs.report;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.creator.workflow.MonthReportWorkFlowStarter;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionParameterInvalid;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionProfileNotExists;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionQueryProfileWithId;
import com.x.report.assemble.control.jaxrs.report.exception.ExceptionStartWorkflowForReport;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 根据汇报ID以及指定的工作ID获取所有的工作计划信息列表
 * 
 * @author O2LEE
 *
 */
public class ActionStartWorkflowWithProfile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStartWorkflowWithProfile.class);

	protected ActionResult<WrapOutBoolean> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			String profileId) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		Report_P_Profile reportProfile = null;
		WrapOutBoolean wrap = new WrapOutBoolean();
		Boolean check = true;

		if (check) {
			if (profileId == null || profileId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionParameterInvalid("参数'profileId'不允许为空！");
				result.error(exception);
			}
		}

		// 查询汇报概要文件是否存在
		if (check) {
			try {
				reportProfile = report_P_ProfileServiceAdv.get(profileId);
				if (reportProfile == null) {
					check = false;
					Exception exception = new ExceptionProfileNotExists(profileId);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionQueryProfileWithId(e, profileId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 查询并且发起流程
		if (check) {
			MonthReportWorkFlowStarter monthReportWorkFlowStarter = new MonthReportWorkFlowStarter();
			try {
				reportProfile = monthReportWorkFlowStarter.startWorkFlow(effectivePerson, reportProfile );
				wrap.setValue( true );
			} catch (Exception e) {
				wrap.setValue(false);
				Exception exception = new ExceptionStartWorkflowForReport(e, profileId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		result.setData(wrap);
		return result;
	}
}