package com.x.attendance.assemble.control.service.v2;

import java.util.Arrays;
import java.util.List;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.UnitTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2ConfigProperties;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_attendance_assemble_control;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class AttendanceV2LeaveManagerService {
    private static Logger logger = LoggerFactory.getLogger(AttendanceV2LeaveManagerService.class);

    public void initDefaultLeaveTypeData() {
        // 初始化考勤假勤类型的默认数据
        if (logger.isDebugEnabled()) {
            logger.debug("初始化考勤假勤类型的默认数据的任务开始...");
        }
        try {
            defaultLeaveTypeData();
            // 更新配置，标记假勤类型已初始化
            updateConfig();
            logger.info("考勤假勤类型的默认数据初始化完成.");
        } catch (Exception e) {
            logger.error(e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("初始化考勤假勤类型的默认数据的任务结束...");
        }
    }

    private void defaultLeaveTypeData() throws Exception {
        List<String> defaultLeaveTypes = Arrays.asList("年假", "病假", "事假", "婚假", "丧假", "产假", "陪产假", "其他");
        Application app = ThisApplication.context().applications()
                .randomWithWeight(x_attendance_assemble_control.class.getName());
        if (app != null) {
            for (String leaveType : defaultLeaveTypes) {
                AttendanceV2LeaveType body = new AttendanceV2LeaveType();
                body.setName(leaveType);
                body.setQuotaType(QuotaTypeEnum.UNLIMITED.getValue());
                body.setUnit(UnitTypeEnum.DAY.getValue());
                WoId woId = ThisApplication.context().applications().postQuery(false, app, "v2/leavemanager/type", body)
                        .getData(WoId.class);
                if (logger.isDebugEnabled()) {
                    logger.debug("默认假勤类型 {} 初始化完成，ID: {}", leaveType, woId.getId());
                }
            }
        } else {
            logger.warn("无法获取到应用实例，无法初始化默认的假勤类型数据.");
        }
    }

    private void updateConfig() throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AttendanceV2Config config;
            List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
            AttendanceV2ConfigProperties properties = new AttendanceV2ConfigProperties();
            properties.setLeaveTypeInitialized(true);
            if (list != null && !list.isEmpty()) {
                config = list.get(0);
            } else {
                config = new AttendanceV2Config();
            }
            config.setProperties(properties);
            emc.beginTransaction(AttendanceV2Config.class);
            emc.persist(config, CheckPersistType.all);
            emc.commit();
        }
    }
}
