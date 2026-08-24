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
        long start = System.currentTimeMillis();
        int policyCount = 0;
        int enqueueCount = 0;
        logger.info("======================新版考勤假期管理策略发放定时器开始执行==============================");
        try {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                logger.info("开始查询启用中的假期发放策略.");
                List<AttendanceV2LeavePolicy> policyList = emc.listEqual(AttendanceV2LeavePolicy.class, AttendanceV2LeavePolicy.active_FIELDNAME, true);
                policyCount = policyList == null ? 0 : policyList.size();
                logger.info("启用中的假期发放策略查询完成, 数量: {}.", policyCount);
                if (policyList == null || policyList.isEmpty()) {
                    return;
                }
                for (AttendanceV2LeavePolicy policy : policyList) {
                    try {
                        logger.info("假期发放策略准备入队, policyId: {}, policyName: {}.", policy.getId(), policy.getPolicyName());
                        QueueAttendanceV2LeavePolicyGrantModel model = new QueueAttendanceV2LeavePolicyGrantModel();
                        model.setPolicy(policy);
                        model.setIsImmediately(false);
                        ThisApplication.queueV2LeavePolicyGrant.send(model);
                        enqueueCount++;
                        logger.info("假期发放策略入队完成, policyId: {}, policyName: {}.", policy.getId(), policy.getPolicyName());
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            logger.info("======================新版考勤假期管理策略发放定时器执行完成, 策略数量: {}, 入队数量: {}, 耗时: {}ms==============================",
                    policyCount, enqueueCount, System.currentTimeMillis() - start);
        }
    }
    
}
