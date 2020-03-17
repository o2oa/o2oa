package com.x.okr.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.okr.assemble.common.date.DateOperation;
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
public class WorkProgressConfirm extends AbstractJob {

	private static  Logger logger = LoggerFactory.getLogger( WorkProgressConfirm.class );
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();	
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	private DateOperation dateOperation = new DateOperation();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		String report_progress = null;
		String nowDateTime = dateOperation.getNowDateTime();
		List<String> ids = null;
		Integer maxWhileCount = 10;
		Integer nowWhileCount = 0;
		boolean check = true;
		
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
			throw new JobExecutionException(e);
		}
		
		//查询所有未完成工作的ID列表, isCompleted = false, progressAnalyseTime不是当前的时间缀
		try {
			ids = okrWorkBaseInfoService.listIdsForNeedProgressAnalyse( nowDateTime, 500 );
		} catch (Exception e) {
			check = true;
			logger.warn( "system list ids for need progress analyse got an exceptin." );
			logger.error(e);
			throw new JobExecutionException(e);
		}
		
		if( check ){
			while( ids != null && !ids.isEmpty() ){
				nowWhileCount ++ ;
				//logger.info( "第"+nowWhileCount+"次查询需要分析的工作列表......" );
				if( nowWhileCount > maxWhileCount ){
					break;
				}
				//根据参数REPORT_PROGRESS的值来进行不同的工作进度分析逻辑。
				for( String id : ids ){
					try {
						okrWorkBaseInfoService.analyseWorkProgress( id, null, report_progress, nowDateTime );
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
		logger.info( "Timertask OKR_WorkProgressConfirm completed and excute success." );
	}
}