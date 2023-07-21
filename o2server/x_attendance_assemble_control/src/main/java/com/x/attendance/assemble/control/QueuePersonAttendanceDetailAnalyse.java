package com.x.attendance.assemble.control;

import java.util.List;
import java.util.Map;

import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

/**
 * 对单个员工的打卡信息进行分析的队列
 */
public class QueuePersonAttendanceDetailAnalyse extends AbstractQueue<String> {

    private static final Logger logger = LoggerFactory.getLogger(QueuePersonAttendanceDetailAnalyse.class);

    @Override
    protected void execute( String detailId ) throws Exception {
        AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
        AttendanceStatisticalCycleServiceAdv statisticalCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
        AttendanceDetailAnalyseServiceAdv detailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
        AttendanceDetailServiceAdv detailServiceAdv = new AttendanceDetailServiceAdv();
        AttendanceDetail record = detailServiceAdv.get( detailId );
        if( record != null ){
            logger.debug("system try to analyse attendance detail for person, Id:" + record.getId() );

            try {
                List<AttendanceWorkDayConfig> workDayConfigList = attendanceWorkDayConfigServiceAdv.getAllWorkDayConfigWithCache( false );
                Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = statisticalCycleServiceAdv.getAllStatisticalCycleMapWithCache( false );

                detailAnalyseServiceAdv.analyseAttendanceDetail( record, workDayConfigList, statisticalCycleMap, false );
                logger.debug( "attendance detail analyse completed.person:" + record.getEmpName() + ", date:" + record.getRecordDateString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            logger.debug("["+record.getEmpName()+"]["+ record.getRecordDateString() +"] attendance detail record analyse task execute completed。" );
        }else{
            logger.warn("attandence detail not exists, id:" + detailId );
        }

    }


}
