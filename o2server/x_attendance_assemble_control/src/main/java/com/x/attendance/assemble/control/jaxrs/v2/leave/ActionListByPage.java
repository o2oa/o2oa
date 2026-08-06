package com.x.attendance.assemble.control.jaxrs.v2.leave;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
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
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class ActionListByPage extends  BaseAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);

    ActionResult<List<Wo>> execute(EffectivePerson person, Integer page, Integer size, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("person {}, page: {}, size: {}", person.getDistinguishedName(), adjustPage, adjustPageSize);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            List<String> userList = null;
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                userList = new ArrayList<>();
                for (String f : wi.getFilterList()) {
                    analysisPerson(userList, f, business);
                }
                userList = new ArrayList<>(new LinkedHashSet<>(userList));
                if (userList.isEmpty()) {
                    LOGGER.warn("没有找到人员信息，查询条件：{}", wi.getFilterList());
                    result.setData(new ArrayList<>());
                    result.setCount(0L);
                    return result;
                }
            }
            Date startDate = null;
            if (StringUtils.isNotEmpty(wi.getStartDate())) {
                DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd);
                startDate = DateTools.parseDateTime(wi.getStartDate() + " 00:00:00");
            }
            Date endDate = null;
            if (StringUtils.isNotEmpty(wi.getEndDate())) {
                DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd);
                endDate = DateTools.parseDateTime(wi.getEndDate() + " 23:59:59");
            }
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            List<AttendanceV2LeaveData> list = business.getAttendanceV2ManagerFactory()
                    .listLeaveDataByPage(adjustPage, adjustPageSize, wi.getPerson(), userList, startDate, endDate);
            result.setData(Wo.copier.copy(list));
            result.setCount(
                    business.getAttendanceV2ManagerFactory().listLeaveDataCount(wi.getPerson(), userList, startDate,
                            endDate));
            return result;
        }
    }

    private void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (StringUtils.isEmpty(filter)) {
            return;
        }
        if (filter.endsWith("@U")) {
            List<String> users = business.organization().person().listWithUnitSubDirect(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("人员DN")
        private String person;

        @FieldDescribe("过滤人员或组织，组织只支持单层: 用户或组织的DN，如xxx@xxx@P、xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("开始日期: YYYY-MM-dd")
        private String startDate;

        @FieldDescribe("结束日期: YYYY-MM-dd")
        private String endDate;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
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


    public static class Wo extends AttendanceV2LeaveData {

        static WrapCopier<AttendanceV2LeaveData, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveData.class, Wo.class, null,
                JpaObject.FieldsInvisible);

    }

}
