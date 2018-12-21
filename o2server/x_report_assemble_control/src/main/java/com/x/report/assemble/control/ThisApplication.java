package com.x.report.assemble.control;

import com.x.base.core.project.Context;
import com.x.report.assemble.control.schedule.Timertask_ReportCreateTask;
import com.x.report.assemble.control.service.Report_S_SettingServiceAdv;

public class ThisApplication {

	public static final String WORKTYPE_DEPT = "部门重点工作";

	public static final String WORKTYPE_PERSON = "自定义工作项";

	protected static Context context;
	
	public static Context context() {
		return context;
	}
	
	public static void init() throws Exception {
		
		//1、初始化（或者检查）配置项
		new Report_S_SettingServiceAdv().initAllSystemConfig();		
		
		// 创建生成汇报：每天8点至17点间，每5分钟执行一次
		//System.out.println(">>>>>>>>>>>>注册定时任务：每5分钟执行一次........");
		context.schedule( Timertask_ReportCreateTask.class, "0 */5 * * * ?" );
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
