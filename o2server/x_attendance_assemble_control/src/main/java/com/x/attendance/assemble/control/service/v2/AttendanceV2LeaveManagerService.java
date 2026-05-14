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

    private final List<String> defaultLeaveTypes = Arrays.asList("年假", "带薪事假", "调休假",
            "产假", "育儿假", "哺乳假", "病假", "婚假", "丧假", "其他");
    private final List<String> defaultLeaveTypeIds = Arrays.asList(
            "49448f7d-086e-4f05-8f2c-f64121588661",
            "e9a3b632-1b12-4d9b-bc75-6e54c86d8a35",
            "2f98e6c7-3a15-4e78-9041-3b562a4d9e12",
            "9b1c2d3e-4f5a-6b7c-8d9e-0f1a2b3c4d5e",
            "782a1b9c-d3e4-4f5a-bc6d-7e8f9a0b1c2d",
            "a1b2c3d4-e5f6-47a8-b9c0-d1e2f3a4b5c6",
            "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            "550e8400-e29b-41d4-a716-446655440000",
            "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
            "8d61239c-4f12-4e92-bc10-72a3b1c4d5e6"
    );
    private final List<QuotaTypeEnum> defaultQuotaTypes = Arrays.asList(
            QuotaTypeEnum.QUOTA, QuotaTypeEnum.QUOTA, QuotaTypeEnum.QUOTA,
            QuotaTypeEnum.UNLIMITED, QuotaTypeEnum.QUOTA, QuotaTypeEnum.UNLIMITED,
            QuotaTypeEnum.UNLIMITED, QuotaTypeEnum.UNLIMITED, QuotaTypeEnum.UNLIMITED,
            QuotaTypeEnum.UNLIMITED
    );

    private void defaultLeaveTypeData() throws Exception {
        for (int i = 0; i < defaultLeaveTypes.size(); i++) {
            String leaveType = defaultLeaveTypes.get(i);
            String leaveTypeId = defaultLeaveTypeIds.get(i);
            AttendanceV2LeaveType body = new AttendanceV2LeaveType();
            body.setId(leaveTypeId);
            body.setName(leaveType);
            body.setOrderNumber((200 - (i * 10)));
            body.setQuotaType(defaultQuotaTypes.get(i).getValue());
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
