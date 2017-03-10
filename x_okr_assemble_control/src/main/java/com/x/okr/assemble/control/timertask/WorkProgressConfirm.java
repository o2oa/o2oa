package com.x.okr.assemble.control.timertask;

import java.util.List;
import java.util.TimerTask;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;

/**
 * 定时代理，定时分析所有未完成的工作的完成进度，将分析结果更新到工作信息里，每天运行一次
 * 此工作与系统配置中的参数REPORT_PROGRESS有关：
 * 1、如果REPORT_PROGRESS的值为OPEN，那么工作完成进度需要用户主动汇报，工作完成进度以最后一次用户提交的汇报中填写的进度百分比为准，
 * 工作是否已经完成，也由用户汇报中的相应数据确定。
 * 2、如果REPORT_PROGRESS的值为CLOSE，那么用户是不需要汇报工作进度的，工作完成进度由系统根据工作开始时间，完成时限和当前的时间定期分析。
 * 如果当前时间已经超过或者等于完成时限，那么工作被视为已经完成。此设置下，工作无超期的概念。
 * 
 * @author LIYI
 *
 */
public class WorkProgressConfirm extends TimerTask {

	private Logger logger = LoggerFactory.getLogger( WorkProgressConfirm.class );
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();	
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private DateOperation dateOperation = new DateOperation();

	public void run() {
		String report_progress = null;
		String nowDateTime = dateOperation.getNowDateTime();
		List<String> ids = null;
		Integer maxWhileCount = 10;
		Integer nowWhileCount = 0;
		boolean check = true;
		if( ThisApplication.getWorkProgressConfirmTaskRunning() ){
			logger.info( "Timertask[WorkTaskProgressConfirm] service is running, wait for next time......" );
			return;
		}
		ThisApplication.setWorkProgressConfirmTaskRunning( true );
		
		//此处编写定时任务的业务逻辑
		try {
			report_progress = okrConfigSystemService.getValueWithConfigCode( "REPORT_PROGRESS" );
			if( report_progress == null || report_progress.isEmpty() ){
				report_progress = "CLOSE";
			}
		} catch (Exception e) {
			report_progress = "CLOSE";
			logger.warn( "system get config got an exception." );
			logger.error(e);
		}
		
		//查询所有未完成工作的ID列表, isCompleted = false, progressAnalyseTime不是当前的时间缀
		try {
			ids = okrWorkBaseInfoService.listIdsForNeedProgressAnalyse( nowDateTime, 500 );
		} catch (Exception e) {
			check = true;
			logger.warn( "system list ids for need progress analyse got an exceptin." );
			logger.error(e);
		}
		
		if( check ){
			while( ids != null && !ids.isEmpty() ){
				nowWhileCount ++ ;
				logger.debug( "第"+nowWhileCount+"次查询需要分析的工作列表......" );
				if( nowWhileCount > maxWhileCount ){
					break;
				}
				//根据参数REPORT_PROGRESS的值来进行不同的工作进度分析逻辑。
				for( String id : ids ){
					try {
						okrWorkBaseInfoService.analyseWorkProgress( id, report_progress, nowDateTime );
					} catch (Exception e) {
						logger.warn( "system analyse work progres got an exceptin." );
						logger.error(e);
					}
				}
				try {
					ids = okrWorkBaseInfoService.listIdsForNeedProgressAnalyse( nowDateTime, 500 );
				} catch (Exception e) {
					check = true;
					logger.warn( "system list ids for need progress analyse got an exceptin." );
					logger.error(e);
					break;
				}
			}
		}
		
		ThisApplication.setWorkProgressConfirmTaskRunning( false );
		logger.debug( "Timertask[WorkTaskProgressConfirm] completed and excute success." );
	}
}