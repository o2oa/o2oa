package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionShiftUsed;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2Shift;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/1/31. Copyright © 2023 O2. All rights reserved.
 */
public class ActionDeleteByManager extends BaseAction {


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        ActionResult<Wo> result = new ActionResult<>();

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getId())) {
                throw new ExceptionEmptyParameter("id");
            }
            Business business = new Business(emc);
            boolean isManager = business.isManager(effectivePerson);
            if (!isManager && StringUtils.isEmpty(wi.getBelongOrgDn())) {
                throw new ExceptionAccessDenied(effectivePerson.getName(), "您不是管理员.");
            }
            AttendanceV2Shift shift = emc.find(wi.getId(), AttendanceV2Shift.class);
            if (shift == null) {
                throw new ExceptionNotExistObject(wi.getId() + "班次");
            }
            // 非管理员，必须属于同一个单位或子单位才能删除
            if (!isManager) {
                if (StringUtils.isEmpty(shift.getBelongOrgDn())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                List<String> unitDNList = business.getAttendanceV2ManagerFactory()
                        .belongOrgs(wi.getBelongOrgDn(), business);
                if (!unitDNList.contains(shift.getBelongOrgDn())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
            }
            List<AttendanceV2Group> groupList = business.getAttendanceV2ManagerFactory()
                    .listGroupWithShiftId(wi.getId());
            if (groupList != null && !groupList.isEmpty()) {
                throw new ExceptionShiftUsed(groupList);
            }
            emc.beginTransaction(AttendanceV2Shift.class);
            emc.delete(AttendanceV2Shift.class, shift.getId());
            emc.commit();
            CacheManager.notify(AttendanceV2Shift.class);// 清除缓存
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }

        return result;
    }

    public static class Wi extends GsonPropertyObject {

        private String id;
        private String belongOrgDn;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBelongOrgDn() {
            return belongOrgDn;
        }

        public void setBelongOrgDn(String belongOrgDn) {
            this.belongOrgDn = belongOrgDn;
        }
    }

    public static class Wo extends WrapOutBoolean {

        private static final long serialVersionUID = -719724843655567310L;
    }
}
