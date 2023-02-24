package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionParticipateConflict;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {


    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);


    /**
     * 处理考勤组 考勤人员 将人员、组织全部换成人员DN
     * @param emc
     * @param business
     * @param groupId
     * @param participateList
     * @param unParticipateList
     * @return
     * @throws Exception
     */
    protected List<String> calTruePersonFromMixList(EntityManagerContainer emc, Business business, String groupId, List<String> participateList, List<String> unParticipateList) throws Exception {
        // 处理考勤组
        List<String> peopleList = new ArrayList<>();
        for (String p : participateList) {
            if (p.endsWith("@P")) {
                peopleList.add(p);
            } else if (p.endsWith("@I")) {
                String person = business.organization().person().getWithIdentity(p);
                peopleList.add(person);
            }else if (p.endsWith("@U")) { // 递归查询人员
                List<String> pList = business.organization().person().listWithUnitSubNested( p );
                peopleList.addAll(pList);
            } else {
                LOGGER.info("错误的标识？ " + p);
            }
        }
        // 删除排除的人员
        if (unParticipateList != null && !unParticipateList.isEmpty()) {
            for (String p: unParticipateList) {
                peopleList.remove(p);
            }
        }
        // 去重复
        HashSet<String> peopleSet = new HashSet<>(peopleList);
        // 判断是否和其它考勤组内的成员冲突
        List<String> conflictPersonInOtherGroup = new ArrayList<>();
        List<AttendanceV2Group> groups = emc.listAll(AttendanceV2Group.class);
        if (groups != null && !groups.isEmpty()) {
            for (String person : peopleSet) {
                for (AttendanceV2Group oldG : groups) {
                    // 自己不用处理
                    if (oldG.getId().equals(groupId)) {
                        continue;
                    }
                    if (oldG.getTrueParticipantList().contains(person)) {
                        conflictPersonInOtherGroup.add(person);
                        break;
                    }
                }
            }
        }
        if (!conflictPersonInOtherGroup.isEmpty()) {
            throw new ExceptionParticipateConflict(conflictPersonInOtherGroup);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("最终考勤组人员数：" + peopleSet.size());
        }

        return new ArrayList<>(peopleSet);
    }

}
