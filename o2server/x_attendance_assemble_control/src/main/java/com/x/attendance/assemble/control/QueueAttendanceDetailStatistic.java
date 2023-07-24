package com.x.attendance.assemble.control;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.service.AttendanceStatisticRequireLogServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

/**
 * 对单个员工的打卡信息进行分析的队列
 */
public class QueueAttendanceDetailStatistic extends AbstractQueue<String> {

    private static final Logger logger = LoggerFactory.getLogger(QueueAttendanceDetailStatistic.class);

    @Override
    protected void execute( String logId ) throws Exception {
        AttendanceStatisticRequireLogServiceAdv statisticRequireLogServiceAdv = new AttendanceStatisticRequireLogServiceAdv();
        AttendanceWorkDayConfigServiceAdv workDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
        AttendanceStatisticServiceAdv statisticServiceAdv = new AttendanceStatisticServiceAdv();
        AttendanceStatisticalCycleServiceAdv statisticalCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
        AttendanceStatisticRequireLog log = statisticRequireLogServiceAdv.get(logId);

        if( log != null ){
            logger.debug("system try to statistic attendance detail, logId:" + logId );
            AttendanceStatisticalCycle attendanceStatisticalCycle  = null;
            List<AttendanceWorkDayConfig> workDayConfigList = null;
            List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = null;

            try {//先查询所有的法定节假日和工作日配置列表
                workDayConfigList = workDayConfigServiceAdv.getAllWorkDayConfigWithCache(false );
            } catch ( Exception e ) {
                logger.warn("【统计】系统在查询当月有打卡记录的员工姓名列表时发生异常！" );
                logger.error(e);
            }

            try{//查询所有的考勤统计周期信息，并且组织成MAP
                statisticalCycleMap = statisticalCycleServiceAdv.getAllStatisticalCycleMapWithCache(false);
            }catch(Exception e){
                logger.warn( "【统计】系统在查询并且组织所有的考勤统计周期信息时发生异常。" );
                logger.error(e);
            }

            //先处理所有的统计错误
            try {
                logger.debug( false, "准备处理恢复的统计错误信息, 所有错误统计将会重新计算......" );
                statisticRequireLogServiceAdv.resetStatisticError();
            } catch (Exception e) {
                logger.warn("【统计】系统在重置统计错误信息时发生异常！" );
                logger.error(e);
            }
            //统计类型:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
            //统计处理状态：WAITING|PROCESSING|COMPLETE|ERROR
            if( StringUtils.equals( "PERSON_PER_MONTH", log.getStatisticType() )){
                logger.debug( false, "系统准备统计[员工每月统计]， 员工：" + log.getStatisticKey() + ", 统计月份:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
                try {
                    attendanceStatisticalCycle = statisticalCycleServiceAdv.getStatisticCycleByEmployee( log, statisticalCycleMap, false );
                } catch (Exception e) {
                    logger.warn("【统计】系统在根据统计需求记录信息查询统计周期信息时发生异常！" );
                    logger.error(e);
                }
                if( attendanceStatisticalCycle != null ){
                    try{
                        statisticServiceAdv.statisticEmployeeAttendanceForMonth( log, attendanceStatisticalCycle, workDayConfigList, statisticalCycleMap);
                    }catch(Exception e){
                        logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
                        logger.error(e);
                    }
                }
            } else if ( StringUtils.equals( "UNIT_PER_MONTH", log.getStatisticType() )){
                logger.debug( false, "系统准备统计[组织每月统计]， 组织：" + log.getStatisticKey() + ", 统计月份:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
                try{
                    statisticServiceAdv.statisticUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap );
                }catch(Exception e){
                    logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
                    logger.error(e);
                }
            } else if ( StringUtils.equals( "TOPUNIT_PER_MONTH", log.getStatisticType() )){
                logger.debug( false, "系统准备统计[顶层组织每月统计]， 顶层组织：" + log.getStatisticKey() + ", 统计月份:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
                try{
                    statisticServiceAdv.statisticTopUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap);
                }catch(Exception e){
                    logger.warn( "【统计】系统在根据需求进行员工月度打卡记录分析结果统计时发生异常。" );
                    logger.error(e);
                }
            } else if ( StringUtils.equals( "UNIT_PER_DAY", log.getStatisticType() )){
                logger.debug( false, "系统准备统计[组织每月统计]， 组织：" + log.getStatisticKey() + ", 统计日期:" + log.getStatisticDay() );
                try{
                    statisticServiceAdv.statisticUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap, false );
                }catch(Exception e){
                    logger.warn( "【统计】系统在根据需求进行组织每日打卡记录分析结果统计时发生异常。" );
                    logger.error(e);
                }
            } else if ( StringUtils.equals( "TOPUNIT_PER_DAY", log.getStatisticType() )){
                logger.debug( false, "系统准备统计[顶层组织每月统计]， 顶层组织：" + log.getStatisticKey() + ", 统计日期:" + log.getStatisticDay() );
                try{
                    statisticServiceAdv.statisticTopUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap );
                }catch(Exception e){
                    logger.warn( "【统计】系统在根据需求进行顶层组织每日打卡记录分析结果统计时发生异常。" );
                    logger.error(e);
                }
            }else{
                logger.warn( "statistic require log can not execute, type:" + log.getStatisticType() );
            }
            logger.debug("["+logId+"] attendance detail record statistic task execute completed。" );
        }else{
            logger.warn("attandence statistic require logId not exists, id:" + logId );
        }
    }
}
