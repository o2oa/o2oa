package com.x.server.console.action;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.core.entity.Report_C_WorkPlan;
import com.x.report.core.entity.Report_C_WorkPlanDetail;
import com.x.report.core.entity.Report_C_WorkPlanNext;
import com.x.report.core.entity.Report_C_WorkPlanNextDetail;
import com.x.report.core.entity.Report_C_WorkProg;
import com.x.report.core.entity.Report_C_WorkProgDetail;
import com.x.report.core.entity.Report_I_Base;
import com.x.report.core.entity.Report_I_Detail;
import com.x.report.core.entity.Report_I_WorkInfo;
import com.x.report.core.entity.Report_I_WorkInfoDetail;
import com.x.report.core.entity.Report_I_WorkTag;
import com.x.report.core.entity.Report_I_WorkTagUnit;
import com.x.report.core.entity.Report_P_MeasureInfo;
import com.x.report.core.entity.Report_P_Permission;
import com.x.report.core.entity.Report_P_Profile;
import com.x.report.core.entity.Report_P_ProfileDetail;
import com.x.report.core.entity.Report_R_CreateTime;
import com.x.report.core.entity.Report_R_View;

public class ActionEraseContentReport extends ActionEraseContentProcessPlatform {

	private static Logger logger = LoggerFactory.getLogger(ActionEraseContentReport.class);

	public boolean execute(String password) throws Exception {
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			logger.print("password not match.");
			return false;
		}
		this.init("report", null);
		addClass(Report_C_WorkPlan.class);
		addClass(Report_C_WorkPlanDetail.class);
		addClass(Report_C_WorkPlanNext.class);
		addClass(Report_C_WorkPlanNextDetail.class);
		addClass(Report_C_WorkProg.class);
		addClass(Report_C_WorkProgDetail.class);
		addClass(Report_I_Base.class);
		addClass(Report_I_Detail.class);
		addClass(Report_I_WorkInfo.class);
		addClass(Report_I_WorkInfoDetail.class);
		addClass(Report_I_WorkTag.class);
		addClass(Report_I_WorkTagUnit.class);
		addClass(Report_P_MeasureInfo.class);
		addClass(Report_P_Permission.class);
		addClass(Report_P_Profile.class);
		addClass(Report_P_ProfileDetail.class);
		addClass(Report_R_CreateTime.class);
		addClass(Report_R_View.class);
		this.run();
		return true;
	}
}