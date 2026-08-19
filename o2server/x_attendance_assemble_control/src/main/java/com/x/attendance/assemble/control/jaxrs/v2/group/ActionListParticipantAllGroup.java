package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class ActionListParticipantAllGroup extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionListParticipantAllGroup.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, person:{}.", effectivePerson.getDistinguishedName());
        }
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                LOGGER.warn("person {} is not manager, can not list all participant.",
                        effectivePerson.getDistinguishedName());
                return result;
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            List<AttendanceV2Group> list = new ArrayList<>();
            if (wi != null && StringUtils.isNotEmpty(wi.getGroupId())) {
                AttendanceV2Group group = emc.find(wi.getGroupId(), AttendanceV2Group.class);
                if (group != null) {
                    list.add(group);
                } else {
                    LOGGER.warn("group {} not found.", wi.getGroupId());
                }
            } else {
                list = emc.listAll(AttendanceV2Group.class);
            }
            if (list == null || list.isEmpty()) {
                return result;
            }
            List<String> participantList = new ArrayList<>();
            for (AttendanceV2Group group : list) {
                if (group.getStatus() != null
                    && group.getStatus() == AttendanceV2Group.status_auto) {
                    continue;  // 跳过自动保存的数据
                }
                participantList.addAll(group.getTrueParticipantList());
            }
            Wo wo = new Wo();
            wo.setParticipantList(participantList);
            result.setData(wo);
        }
        return result;
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("考勤组ID, 不传就是全部考勤组")
        private String groupId;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("成员 DN 列表")
        private List<String> participantList;

        public List<String> getParticipantList() {
            return participantList;
        }

        public void setParticipantList(List<String> participantList) {
            this.participantList = participantList;
        }
    }
}
