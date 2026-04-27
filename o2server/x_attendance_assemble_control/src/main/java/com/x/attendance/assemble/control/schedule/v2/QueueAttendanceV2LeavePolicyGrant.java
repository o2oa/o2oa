package com.x.attendance.assemble.control.schedule.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.ExpireTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantScopeTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantTypeEnum;
import com.x.attendance.assemble.control.schedule.v2.model.QueueAttendanceV2LeavePolicyGrantModel;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicyGrantAmountTypeProperties;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;

public class QueueAttendanceV2LeavePolicyGrant extends AbstractQueue<QueueAttendanceV2LeavePolicyGrantModel> {

    private static final Logger logger = LoggerFactory.getLogger(QueueAttendanceV2LeavePolicyGrant.class);

    @Override
    protected void execute(QueueAttendanceV2LeavePolicyGrantModel t) throws Exception {
        if (t == null || t.getPolicy() == null) {
            logger.warn("QueueAttendanceV2LeavePolicyGrantModel is null or policy is null, ignore this task.");
            return;
        }
        AttendanceV2LeavePolicy policy = t.getPolicy();
        Date today = new Date();
        String todayStr = DateTools.formatDate(today);
        if (!BooleanUtils.isTrue(t.getIsImmediately()) && !todayStr.equals(policy.getGrantNextExecuteTime())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Today is {}, but policy {} next execute time is {}, ignore this task.", todayStr,
                        policy.getId(), policy.getGrantNextExecuteTime());
            }
            return;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            String grantPeriod = null;
            if (GrantTypeEnum.YEARLY.getValue().equals(policy.getGrantType())) {
                grantPeriod = DateTools.format(today, "yyyy");
            } else if (GrantTypeEnum.MONTHLY.getValue().equals(policy.getGrantType())) {
                grantPeriod = DateTools.format(today, "yyyy-MM");
            } else if (GrantTypeEnum.ONE_TIME.getValue().equals(policy.getGrantType())) {
                grantPeriod = AttendanceV2LeaveManager.ONE_TIME;
            }
            if (StringUtils.isBlank(grantPeriod)) {
                logger.warn("Grant period is blank for policy {}, skip this task.", policy.getId());
                return;
            }
            List<String> userList = new ArrayList<>();
            // 全员
            if (GrantScopeTypeEnum.ALL.getValue().equals(policy.getGrantScopeType())) {
                userList = business.organization().person().listAll();
            } else if (GrantScopeTypeEnum.DEPARTMENT.getValue().equals(policy.getGrantScopeType())) {
                // 组织
                if (policy.getGrantScopeList() != null && !policy.getGrantScopeList().isEmpty()) {
                    for (String filter : policy.getGrantScopeList()) {
                        analysisPerson(userList, filter, business);
                    }
                }
            }
            if (userList == null || userList.isEmpty()) {
                logger.warn("User list is empty for policy {}, skip this task.", policy.getId());
                return;
            }
            // userList 去重
            userList = new ArrayList<>(new java.util.HashSet<>(userList));
            // 循环发放
            for (String user : userList) {
                // 发放额度
                double grantAmount = 0.0;
                if (GrantTypeEnum.YEARLY.getValue().equals(policy.getGrantType())) {
                    AttendanceV2LeavePolicyGrantAmountTypeProperties grantAmountTypeProperties = policy
                            .getGrantAmountType();
                    if (grantAmountTypeProperties != null
                            && "SERVICELEN".equalsIgnoreCase(grantAmountTypeProperties.getType())) {
                        Person person = business.organization().person().getObject(grantPeriod);
                        Date boardDate = person.getBoardDate();
                        if (boardDate != null) {
                            double yearGap = (today.getTime() - boardDate.getTime()) / (1000L * 60 * 60 * 24 * 365);
                            grantAmount = grantAmountTypeProperties.calculateGrantAmount(yearGap);
                        } else {
                            logger.warn(
                                    "Person {} board date is null, cannot calculate years of service, set grant amount to 0.",
                                    person.getName());
                            grantAmount = 0.0;
                        }
                    } else {
                        grantAmount = grantAmountTypeProperties.calculateGrantAmount(-1.0);
                    }

                } else {
                    grantAmount = policy.getGrantAmount() != null ? policy.getGrantAmount() : 0.0;
                }
                if (grantAmount <= 0) {
                    logger.warn("Policy {} grant amount is {}, skip granting for user {}.", policy.getId(), grantAmount,
                            user);
                    continue;
                }
                // todo grantPeriod policyId person 查询是否已经有记录了，避免重复发放
                emc.beginTransaction(AttendanceV2LeaveLedger.class);
                AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
                ledger.setLeaveTypeId(policy.getLeaveTypeId());
                ledger.setPerson(user);
                ledger.setPolicyId(policy.getId());
                ledger.setGrantPeriod(grantPeriod);
                ledger.setUsedAmount(0.0);
                ledger.setRemainingAmount(0.0);
                ledger.setGrantAmount(grantAmount);
                ledger.setGrantTime(today);
                if (ExpireTypeEnum.RELATIVE.getValue().equals(policy.getExpireType())) {
                    int addDay = policy.getExpireValue() != null ? policy.getExpireValue() : 0;
                    if (GrantTypeEnum.YEARLY.getValue().equals(policy.getGrantType())) {
                        addDay = addDay * 365;
                    } else if (GrantTypeEnum.MONTHLY.getValue().equals(policy.getGrantType())) {
                        addDay = addDay * 30;
                    }
                    Date expireTime = DateTools.addDay(today, addDay);
                    ledger.setExpireTime(expireTime);
                } //
                emc.persist(ledger, CheckPersistType.all);
                emc.commit();
            }
            logger.info("发放完成，政策ID: {}, 用户数量: {} 。 开始更新下一次执行时间", policy.getId(), userList.size());
            // 更新下一次执行时间
            AttendanceV2LeaveManager.calculateNextExecutionTimeForLeavePolicy(policy);
            emc.beginTransaction(AttendanceV2LeavePolicy.class);
            AttendanceV2LeavePolicy policyOld = emc.find(policy.getId(), AttendanceV2LeavePolicy.class);
            policy.copyTo(policyOld, JpaObject.FieldsUnmodify);
            emc.check(policyOld, CheckPersistType.all);
            emc.commit();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤假期管理策略 {} 发放 执行完成==============================", policy.getPolicyName());
        }
    }

    // 解析人员列表，把组织下人员都查询出来放入 userList 中
    private void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (filter.endsWith("@U")) { // 组织转化成人员列表 递归
            List<String> users = business.organization().person().listWithUnitSubNested(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

}
