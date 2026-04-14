package com.x.attendance.assemble.control.service.v2;

import java.util.Arrays;
import java.util.List;

import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.UnitTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.attendance.entity.v2.AttendanceV2ConfigProperties;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
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
            // 初始化默认假勤类型数据
            defaultLeaveTypeData();
            updateConfig();
            logger.info("考勤假勤类型的默认数据初始化完成.");
        } catch (Exception e) {
            logger.error(e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("初始化考勤假勤类型的默认数据的任务结束...");
        }
    }

    private final List<String> defaultLeaveTypes = Arrays.asList("年假", "病假", "事假", "婚假", "丧假", "产假", "陪产假", "其他");
    private final List<String> defaultLeaveTypeIds = Arrays.asList("f8743f00-f193-4526-8de3-ff425d398f93",
            "ed809d9a-4fbb-4c23-885f-4f6e7e7dc339", "eac27748-3baf-474f-9cc5-a508a919c254",
            "e0a055a4-08d3-4466-8680-2a9f13d46194", "bc30bffa-dd23-463d-9f41-1bc7b0351082",
            "8eca0b5c-aa0c-4b22-9857-2e1e6bfa22f4", "6505c460-8efb-4dc8-a44d-ba2eb3706fd2",
            "25d425de-a9bd-43f3-92f6-1cc88d54e262");

    private void defaultLeaveTypeData() throws Exception {
        for (int i = 0; i < defaultLeaveTypes.size(); i++) {
            String leaveType = defaultLeaveTypes.get(i);
            String leaveTypeId = defaultLeaveTypeIds.get(i);
            AttendanceV2LeaveType body = new AttendanceV2LeaveType();
            body.setId(leaveTypeId);
            body.setName(leaveType);
            body.setOrderNumber((100 - (i * 10)));
            body.setQuotaType(QuotaTypeEnum.UNLIMITED.getValue());
            body.setUnit(UnitTypeEnum.DAY.getValue());
            saveLeaveType(body);
        }
    }

    private void saveLeaveType(AttendanceV2LeaveType leaveType) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(AttendanceV2LeaveType.class);
            emc.persist(leaveType, CheckPersistType.all);
            emc.commit();
            if (logger.isDebugEnabled()) {
                logger.debug("默认假勤类型 {} 初始化完成，ID: {}", leaveType, leaveType.getId());
            }
        }

    }

    private void updateConfig() {
        try {
            EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
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
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
