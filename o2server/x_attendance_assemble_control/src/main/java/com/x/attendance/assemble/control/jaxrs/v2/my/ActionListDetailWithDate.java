package com.x.attendance.assemble.control.jaxrs.v2.my;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fancyLou on 2023/3/9.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListDetailWithDate extends BaseAction {


    ActionResult<List<Wo>> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {

        ActionResult<List<Wo>> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getStartDate())) {
                throw new ExceptionEmptyParameter("开始日期");
            }
            if (StringUtils.isEmpty(wi.getEndDate())) {
                throw new ExceptionEmptyParameter("结束日期");
            }
            Date startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd); // 检查格式
            Date endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd); // 检查格式
            if (startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            boolean showRest = false;
            if (BooleanUtils.isTrue(wi.getShowRest())) {
                showRest = true;
            }
            Business business = new Business(emc);
            List<Wo> wos = new ArrayList<>();
            List<AttendanceV2Detail> list = business.getAttendanceV2ManagerFactory().listDetailWithPersonAndStartEndDate(person.getDistinguishedName(), wi.getStartDate(), wi.getEndDate());
            if (list != null && !list.isEmpty()) {
                for (AttendanceV2Detail detail : list) {
                    if (showRest || detail.getWorkDay()) {
                        Wo wo = Wo.copier.copy(detail);
                        List<String> ids = detail.getRecordIdList();
                        if (ids != null && !ids.isEmpty()) {
                            List<AttendanceV2CheckInRecord> recordList = new ArrayList<>();
                            for (String id : ids) {
                                AttendanceV2CheckInRecord record = emc.find(id, AttendanceV2CheckInRecord.class);
                                if (record != null) {
                                    recordList.add(record);
                                }
                            }
                            wo.setRecordList(recordList);
                        }
                        wos.add(wo);
                    }
                }
            }
            result.setData(wos);
            return result;
        }
    }



    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 6842439549150552191L;
        @FieldDescribe("开始日期")
        private String startDate;
        @FieldDescribe("结束日期")
        private String endDate;
        @FieldDescribe("是否显示休息日")
        private Boolean showRest;

        public Boolean getShowRest() {
            return showRest;
        }

        public void setShowRest(Boolean showRest) {
            this.showRest = showRest;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }

    public static class Wo extends AttendanceV2Detail {

        private static final long serialVersionUID = 4645923067324854260L;
        static WrapCopier<AttendanceV2Detail,  Wo> copier = WrapCopierFactory.wo(AttendanceV2Detail.class,  Wo.class, null,
                JpaObject.FieldsInvisible);

        @FieldDescribe("打卡记录")
        private List<AttendanceV2CheckInRecord> recordList;

        public List<AttendanceV2CheckInRecord> getRecordList() {
            return recordList;
        }

        public void setRecordList(List<AttendanceV2CheckInRecord> recordList) {
            this.recordList = recordList;
        }
    }
}
