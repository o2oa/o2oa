package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionDeleteByManager extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getId())) {
                throw new ExceptionEmptyParameter("id");
            }
            Business business = new Business(emc);
            boolean isManager = business.isManager(effectivePerson);
            if(!isManager && (wi.getBelongs() == null || wi.getBelongs().isEmpty())){
                throw new ExceptionAccessDenied(effectivePerson.getName(), "您不是管理员.");
            }
            AttendanceV2Group group = emc.find(wi.getId(), AttendanceV2Group.class);
            if (group == null) {
                throw new ExceptionNotExistObject(wi.getId()+"考勤组");
            }
            // 非管理员，必须属于同一个单位或子单位才能删除
            if (!isManager) {
                if (StringUtils.isEmpty(group.getBelongOrgDn())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                List<String> unitDNList = business.getAttendanceV2ManagerFactory()
                        .belongOrgs(wi.getBelongs(), business);
                if (!unitDNList.contains(group.getBelongOrgDn())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
            }

            emc.beginTransaction(AttendanceV2Group.class);
            emc.delete(AttendanceV2Group.class, group.getId());
            emc.commit();
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        }

        return result;
    }

    public static class Wi extends GsonPropertyObject {

        private String id;
        @FieldDescribe("所属组织DN")
        private List<String> belongs;


        public List<String> getBelongs() {
            return belongs;
        }

        public void setBelongs(List<String> belongs) {
            this.belongs = belongs;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }
    public static class Wo extends WrapOutBoolean {


        private static final long serialVersionUID = -3249622798550098407L;
    }
}
