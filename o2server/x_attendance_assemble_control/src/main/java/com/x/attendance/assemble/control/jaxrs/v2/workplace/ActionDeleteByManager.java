package com.x.attendance.assemble.control.jaxrs.v2.workplace;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWorkPlaceUsed;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionDeleteByManager extends BaseAction {


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getId())) {
            throw new ExceptionEmptyParameter("标识");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            boolean isManager = business.isManager(effectivePerson);
            if(!isManager && (wi.getBelongs() == null || wi.getBelongs().isEmpty())){
                throw new ExceptionAccessDenied(effectivePerson.getName(), "您不是管理员.");
            }
            AttendanceV2WorkPlace workPlace = emc.find(wi.getId(), AttendanceV2WorkPlace.class);
            if (workPlace == null) {
                throw new ExceptionNotExistObject(wi.getId()+"工作地点");
            }
            // 非管理员，必须属于同一个单位或子单位才能删除
            if (!isManager) {
                if (StringUtils.isEmpty(workPlace.getBelongOrgDn())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                List<String> unitDNList = business.getAttendanceV2ManagerFactory().belongOrgs(wi.getBelongs(), business);
                if (!unitDNList.contains(workPlace.getBelongOrgDn())) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
            }
            List<AttendanceV2Group> groupList = business.getAttendanceV2ManagerFactory().listGroupWithWorkPlaceId(wi.getId());
            if (groupList != null && !groupList.isEmpty()) {
                throw new ExceptionWorkPlaceUsed(groupList);
            }
            emc.beginTransaction(AttendanceV2WorkPlace.class);
            emc.delete(AttendanceV2WorkPlace.class, workPlace.getId());
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

        private static final long serialVersionUID = -719724843655567310L;
    }
}
