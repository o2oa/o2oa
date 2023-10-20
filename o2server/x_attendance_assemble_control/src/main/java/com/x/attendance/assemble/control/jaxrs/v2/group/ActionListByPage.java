package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2GroupWorkDayProperties;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.List;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListByPage extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
            throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson.getDistinguishedName(), page, size);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            String assistAdmin = null;
            // 不是管理员 只查询协助管理员的数据
            if(!business.isManager(effectivePerson)){
                assistAdmin = effectivePerson.getDistinguishedName();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<AttendanceV2Group> list = business.getAttendanceV2ManagerFactory().listGroupWithNameByPage(adjustPage,
                    adjustPageSize, assistAdmin, wi.getName());
            List<Wo> wos = Wo.copier.copy(list);
            if (wos != null && !wos.isEmpty()) {
                for (Wo group : wos) {
                    // 只需要数量，前端不需要显示整个列表
                    group.setTrueParticipantSize(group.getTrueParticipantList().size());
                    group.setTrueParticipantList(null);
                    // 班次对象返回
                    if (group.getWorkDateProperties() != null && AttendanceV2Group.CHECKTYPE_Fixed.equals( group.getCheckType())) {
                        AttendanceV2GroupWorkDayProperties properties = group.getWorkDateProperties();
                        setPropertiesShiftData(business, properties);
                    }
                }
            }
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory().groupCountWithName(assistAdmin, wi.getName()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = -4138592167949224238L;
        @FieldDescribe("考勤组名称")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static class Wo extends AttendanceV2Group {

        private static final long serialVersionUID = -7147244243285527997L;

        @FieldDescribe("考勤成员数量")
        private int trueParticipantSize;

        @FieldDescribe("考勤组的班次对象")
        private AttendanceV2Shift shift;


        public int getTrueParticipantSize() {
            return trueParticipantSize;
        }

        public void setTrueParticipantSize(int trueParticipantSize) {
            this.trueParticipantSize = trueParticipantSize;
        }

        public AttendanceV2Shift getShift() {
            return shift;
        }

        public void setShift(AttendanceV2Shift shift) {
            this.shift = shift;
        }



        static WrapCopier<AttendanceV2Group, Wo> copier = WrapCopierFactory.wo(AttendanceV2Group.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
