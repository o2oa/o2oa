package com.x.report.assemble.control.creator;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.creator.document.month.ReportDocumentCreator;
import com.x.report.assemble.control.creator.profile.MonthReportProfileCreator;
import com.x.report.assemble.control.creator.workflow.MonthReportWorkFlowStarter;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.assemble.control.service.Report_P_ProfileServiceAdv;
import com.x.report.core.entity.Report_P_Profile;

/**
 * 月度汇报创建生成服务类，包含唯一服务方法<br/>
 * public Boolean create(ReportCreateFlag flag)
 * 
 * @author O2LEE
 *
 */
public class CreatorForMonthReport implements ReportCreatorInf {

	private static Logger logger = LoggerFactory.getLogger(CreatorForMonthReport.class);
	
	private Report_P_ProfileServiceAdv report_P_ProfileServiceAdv = new Report_P_ProfileServiceAdv();

	/**
	 * 生成指定汇报并且发起汇报流程<br/>
	 * 
	 * 1、根据指定的应用模块，查询汇报依据，组织【汇报概要文件】，持久化到数据库<br/>
	 * 2、再依据【汇报概要文件】文件生成汇报文档并且启动汇报流程<br/>
	 * 1) 根据【汇报概文件】要为依据，创建所有个人以及部门的汇报文档<br/>
	 * 2) 如果【汇报文档】成功生成，那么开始启动汇报流程<br/>
	 * 
	 * @throws Exception
	 */
	@Override
	public Boolean create(EffectivePerson effectivePerson, ReportCreateFlag flag ) throws Exception {

		Report_P_Profile recordProfile = null;

		Boolean success = true;

		// =========================================
		// 1、根据要求生成【汇报概要文件】，并且持久化
		// =========================================
		try {
			logger.info( ">>>>>>>>>>根据要求生成【汇报概要文件】，并且持久化......");
			recordProfile = new MonthReportProfileCreator().createProfile(effectivePerson, flag);
			if (recordProfile == null) {
				logger.warn("警告：【汇报概要文件】未成功生成，无法生成汇报文档。");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
			logger.warn("错误：【汇报概要文件】生成时发生异常!");
			logger.error(e);
		}

		// 判断【汇报概要文件】是否生成成功，如果成功，则依据其ID，进行下一步的汇报文档生成，汇报流程启动工作
		// 后续步骤：根据【汇报概要文件】来创建所有的组织和个人汇报文档, 生成完成后，再发起流程
		// =============================================================
		// 2、根据获取到的快照信息为依据，生成所有个人以及部门的【汇报文档】
		// =============================================================
		if (success && recordProfile != null) {
			try {
				logger.info( ">>>>>>>>>>根据获取到的快照信息为依据，生成所有个人以及部门的【汇报文档】......");
				recordProfile = new ReportDocumentCreator().create(effectivePerson, recordProfile );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ===================================================
		// 3、如果【汇报文档】成功生成，那么开始【启动汇报流程】
		// ===================================================
		if (success && recordProfile != null) {
			try {
				logger.info( ">>>>>>>>>>【汇报文档】成功生成，开始【启动汇报流程】");
				recordProfile = new MonthReportWorkFlowStarter().startWorkFlow( effectivePerson, recordProfile );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (success) {
			try {
				recordProfile.setCreateSuccess( true );
				logger.info( ">>>>>>>>>>更新汇报启动概要文件！");
				report_P_ProfileServiceAdv.updateWithId( recordProfile ) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
}
