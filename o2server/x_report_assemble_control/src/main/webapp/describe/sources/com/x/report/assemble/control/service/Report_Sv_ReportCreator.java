package com.x.report.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.report.assemble.control.creator.CreatorForDailyReport;
import com.x.report.assemble.control.creator.CreatorForMonthReport;
import com.x.report.assemble.control.creator.CreatorForWeekReport;
import com.x.report.assemble.control.creator.ReportCreatorInf;
import com.x.report.assemble.control.schedule.bean.ReportCreateFlag;
import com.x.report.assemble.control.schedule.exception.ExceptionReportInfoCreate;

/**
 * 汇报生成服务类
 * @author O2LEE
 *
 */
public class Report_Sv_ReportCreator{

	private Logger logger = LoggerFactory.getLogger(Report_Sv_ReportCreator.class);	
	
	/**
	 * 尝试启动汇报
	 * @param nextReportTime
	 * @return
	 */
	public Boolean create( Date nextReportTime ) {
		EffectivePerson person = EffectivePerson.anonymous();
		return create( person, nextReportTime );
	}
	
	/**
	 * 根据指定的日期，创建在该日期需要生成的汇报内容，并且启动相应的流程进行审批<br/>
	 * 
	 * 1、得根据配置来计算汇报的时间以及是否需要发起汇报<br/>
	    2、判断上一次汇报是否已经成功生成<br/>
	    3、根据上一次汇报时间来计算下一次汇报的具体时间<br/><br/>
	 * 如果date不为空，那么以传入的date为准到启动汇报（如果汇报未生成过）<br/>
	 * 如果date为空，那么先按数据库记录的下一次汇报时间来启动汇报，如果数据库内无记录，那么按当前时间进行汇报时间计算得出一个正确的汇报时间。<br/>
	 * 
	 * @param effectivePerson
	 * @param date
	 * @return
	 */
	public Boolean create( EffectivePerson effectivePerson, Date date ) {
		
		ReportCreatorInf reportCreator = null;
		
		//查询需要汇报的信息列表：汇报类别，汇报周期信息
		List<ReportCreateFlag> flags = new Report_Sv_ReportFlag().getFlags( effectivePerson, date );
		
		if( flags != null && !flags.isEmpty() ) {
			for( ReportCreateFlag flag : flags ) {
				switch ( flag.getReportType() ) {
		            case MONTHREPORT:
		            	reportCreator = new CreatorForMonthReport();
		                break;
		            case WEEKREPORT:
		            	reportCreator = new CreatorForWeekReport();
		                break;
		            case DAILYREPORT:
		            	reportCreator = new CreatorForDailyReport();
		                break;
	            }
				if( reportCreator != null ) {
					try {
						reportCreator.create( effectivePerson, flag );
					} catch (Exception e) {
						Exception exception = new ExceptionReportInfoCreate( e, "系统在创建月度汇报信息时发生异常！" );
						logger.error( exception );
						e.printStackTrace();
					}
				}
			}
		}else {
			logger.info( ">>>>>>>>>>>>汇报需求判断: 尚无汇报生成需求，汇报需求判断逻辑完成。" );
		}
		return true;
	}
	
}
