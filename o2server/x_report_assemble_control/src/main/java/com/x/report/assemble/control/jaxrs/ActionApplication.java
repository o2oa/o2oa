package com.x.report.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.report.assemble.control.jaxrs.export.DataExportAction;
import com.x.report.assemble.control.jaxrs.extcontent.ReportExtPropertyAction;
import com.x.report.assemble.control.jaxrs.profile.ReportProfileAction;
import com.x.report.assemble.control.jaxrs.report.ReportInfoAction;
import com.x.report.assemble.control.jaxrs.reportstat.ReportStatAction;
import com.x.report.assemble.control.jaxrs.setting.ReportSettingAction;
import com.x.report.assemble.control.jaxrs.workinfo.ReportWorkInfoAction;
import com.x.report.assemble.control.jaxrs.workinfo.StrategyWorkConfigAction;
import com.x.report.assemble.control.jaxrs.workinfo.StrategyWorkSnapAction;
import com.x.report.assemble.control.jaxrs.workplan.ReportWorkPlanAction;
import com.x.report.assemble.control.jaxrs.workplan.ReportWorkPlanNextAction;
import com.x.report.assemble.control.jaxrs.workprog.ReportWorkProgAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		
		this.classes.add( ReportSettingAction.class);
		
		this.classes.add( ReportInfoAction.class);
		this.classes.add( ReportWorkPlanAction.class);
		this.classes.add( ReportWorkPlanNextAction.class);
		this.classes.add( ReportWorkProgAction.class);
		this.classes.add( ReportProfileAction.class);
		this.classes.add( ReportStatAction.class);
		this.classes.add( ReportWorkInfoAction.class);
		this.classes.add( StrategyWorkConfigAction.class);
		this.classes.add( StrategyWorkSnapAction.class);
		this.classes.add( DataExportAction.class);

		this.classes.add( ReportExtPropertyAction.class);
		return this.classes;
	}

}