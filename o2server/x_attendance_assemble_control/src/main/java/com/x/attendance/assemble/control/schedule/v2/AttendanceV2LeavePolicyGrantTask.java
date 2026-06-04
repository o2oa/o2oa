package com.x.attendance.assemble.control.schedule.v2;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.schedule.v2.model.QueueAttendanceV2LeavePolicyGrantModel;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

public class AttendanceV2LeavePolicyGrantTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2LeavePolicyGrantTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤假期管理策略发放定时器开始执行==============================");
        }
        try {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                List<AttendanceV2LeavePolicy> policyList = emc.listEqual(AttendanceV2LeavePolicy.class, AttendanceV2LeavePolicy.active_FIELDNAME, true);
                policyList.forEach(policy -> {
                    try {
                        QueueAttendanceV2LeavePolicyGrantModel model = new QueueAttendanceV2LeavePolicyGrantModel();
                        model.setPolicy(policy);
                        model.setIsImmediately(false);
                        ThisApplication.queueV2LeavePolicyGrant.send(model);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                });
            }
        }catch (Exception e) {
            logger.error( e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤假期管理策略发放定时器执行完成==============================");
        }
    }
    
}
