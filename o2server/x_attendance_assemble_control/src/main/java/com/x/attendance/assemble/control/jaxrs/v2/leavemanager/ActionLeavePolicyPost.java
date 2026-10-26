package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.ExpireTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantScopeTypeEnum;
import com.x.attendance.assemble.control.schedule.v2.model.QueueAttendanceV2LeavePolicyGrantModel;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.CronTools;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionLeavePolicyPost extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeavePolicyPost.class);

    ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(person)) {
                throw new ExceptionAccessDenied(person);
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

            if (StringUtils.isBlank(wi.getPolicyName())) {
                throw new ExceptionEmptyParameter("规则名称");
            }
            // 名称不能重复
            List<AttendanceV2LeavePolicy> checkRepetitive = emc.listEqualAndEqual(
                    AttendanceV2LeavePolicy.class,
                    AttendanceV2LeavePolicy.policyName_FIELDNAME, wi.getPolicyName(),
                    AttendanceV2LeavePolicy.active_FIELDNAME, true);
            if (checkRepetitive != null && !checkRepetitive.isEmpty()) {
                for (AttendanceV2LeavePolicy check : checkRepetitive) {
                    if (check.getPolicyName().equals(wi.getPolicyName()) && !check.getId()
                            .equals(wi.getId())) {
                        throw new ExceptionWithMessage("规则名称已存在");
                    }
                }
            }
            if (StringUtils.isBlank(wi.getLeaveTypeId())) {
                throw new ExceptionEmptyParameter("假期类型ID");
            }
            if (StringUtils.isEmpty(wi.getGrantCron()) || !CronTools.available(wi.getGrantCron())) {
                throw new ExceptionWithMessage("定时器表达式错误！");
            }
            if (StringUtils.isBlank(wi.getGrantScopeType())
                || (!GrantScopeTypeEnum.ALL.getValue().equals(wi.getGrantScopeType())
                    && !GrantScopeTypeEnum.DEPARTMENT.getValue().equals(wi.getGrantScopeType()))) {
                throw new ExceptionEmptyParameter("发放范围");
            }
            if (GrantScopeTypeEnum.DEPARTMENT.getValue().equals(wi.getGrantScopeType())
                && (wi.getGrantScopeList() == null || wi.getGrantScopeList().isEmpty())) {
                throw new ExceptionEmptyParameter("发放范围列表");
            }
            // 全员 校验是否有同类型的了 ，
            if (GrantScopeTypeEnum.ALL.getValue().equals(wi.getGrantScopeType())) {
                List<AttendanceV2LeavePolicy> checkTypeAndScopeAll = emc.listEqualAndEqualAndEqual(
                        AttendanceV2LeavePolicy.class,
                        AttendanceV2LeavePolicy.leaveTypeId_FIELDNAME,
                        wi.getLeaveTypeId(), AttendanceV2LeavePolicy.grantScopeType_FIELDNAME,
                        GrantScopeTypeEnum.ALL.getValue(), AttendanceV2LeavePolicy.active_FIELDNAME,
                        true);
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "checkTypeAndScopeAll size: {}, leaveTypeId: {}, grantScopeType: {} , id: {}",
                            checkTypeAndScopeAll == null ? 0 : checkTypeAndScopeAll.size(),
                            wi.getLeaveTypeId(),
                            GrantScopeTypeEnum.ALL.getValue(), wi.getId());
                }
                if (checkTypeAndScopeAll != null && !checkTypeAndScopeAll.isEmpty()) {
                    for (AttendanceV2LeavePolicy check : checkTypeAndScopeAll) {
                        if (!check.getId().equals(wi.getId())) {
                            throw new ExceptionWithMessage("当前假期类型全员已有配置规则");
                        }
                    }
                }
            } else if (GrantScopeTypeEnum.DEPARTMENT.getValue().equals(wi.getGrantScopeType())) {
                // 部门 校验部门列表中是否有同类型的了
                List<AttendanceV2LeavePolicy> checkTypeAndScopeDepartment = emc.listEqualAndEqualAndEqual(
                        AttendanceV2LeavePolicy.class,
                        AttendanceV2LeavePolicy.leaveTypeId_FIELDNAME,
                        wi.getLeaveTypeId(), AttendanceV2LeavePolicy.grantScopeType_FIELDNAME,
                        GrantScopeTypeEnum.DEPARTMENT.getValue(),
                        AttendanceV2LeavePolicy.active_FIELDNAME, true);
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "checkTypeAndScopeDepartment size: {}, leaveTypeId: {}, grantScopeType: {} , id: {}",
                            checkTypeAndScopeDepartment == null ? 0
                                    : checkTypeAndScopeDepartment.size(),
                            wi.getLeaveTypeId(), GrantScopeTypeEnum.DEPARTMENT.getValue(),
                            wi.getId());
                }
                if (checkTypeAndScopeDepartment != null && !checkTypeAndScopeDepartment.isEmpty()) {
                    for (AttendanceV2LeavePolicy check : checkTypeAndScopeDepartment) {
                        List<String> checkGrantScopeList = check.getGrantScopeList();
                        if (!check.getId().equals(wi.getId()) && checkGrantScopeListHasSamePerson(
                                checkGrantScopeList, check.getGrantExcludeList(),
                                wi.getGrantScopeList(), wi.getGrantExcludeList(), business)) {
                            throw new ExceptionWithMessage("当前假期类型部门列表中有重复的人员");
                        }
                    }
                }
            }
            // 使用脚本
            if (BooleanUtils.isTrue(wi.getGrantAmountTypeUseScript()) && StringUtils.isEmpty(wi.getGrantScript())) {
                throw new ExceptionWithMessage("额度发放执行脚本不能为空");
            }
            // 不使用脚本，发放额度不能为空且不能小于0
            if (BooleanUtils.isFalse(wi.getGrantAmountTypeUseScript()) && (wi.getGrantAmount() == null || wi.getGrantAmount() < 0)) {
                throw new ExceptionEmptyParameter("发放额度");
            }
            if (StringUtils.isBlank(wi.getExpireType()) ||  !ExpireTypeEnum.isValidateKey(wi.getExpireType()) ) {
                throw new ExceptionEmptyParameter("过期类型");
            }
            if (wi.getExpireType().equals(ExpireTypeEnum.AFTER_GRANT.getValue())) {
                if (wi.getExpireValue() == null || wi.getExpireValue() <= 0) {
                    throw new ExceptionEmptyParameter("过期日期配置");
                }
            } else {
                if (StringUtils.isBlank(wi.getExpireMonthDay())) {
                    throw new ExceptionEmptyParameter("过期日期配置");
                }
                if (!isValidExpireMonthDay(wi.getExpireMonthDay())) {
                    throw new ExceptionWithMessage("过期日期配置格式错误，请使用 MM-dd 格式");
                }
            }

            if (BooleanUtils.isTrue(wi.getCarryForward())
                && (wi.getMaxCarryForward() == null || wi.getMaxCarryForward() < 0)) {
                throw new ExceptionEmptyParameter("最大结转额度");
            }

            AttendanceV2LeavePolicy leavePolicy = Wi.copier.copy(wi);
            if (ExpireTypeEnum.AFTER_GRANT.getValue().equals(leavePolicy.getExpireType())) {
                leavePolicy.setExpireMonthDay(null);
            } else {
                leavePolicy.setExpireValue(null);
            }
            emc.beginTransaction(AttendanceV2LeavePolicy.class);
            if (StringUtils.isBlank(wi.getId())) {
                emc.persist(leavePolicy, CheckPersistType.all);
                Wo wo = new Wo();
                wo.setId(leavePolicy.getId());
                result.setData(wo);
            } else {
                AttendanceV2LeavePolicy old = emc.find(wi.getId(), AttendanceV2LeavePolicy.class);
                if (old != null) {
                    leavePolicy.copyTo(old, JpaObject.FieldsUnmodify);
                    emc.check(old, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(old.getId());
                    result.setData(wo);
                } else {
                    leavePolicy.setGrantLastExecuteTime(new Date()); // 新增的时候添加执行时间，放在定时器错误执行
                    emc.persist(leavePolicy, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(leavePolicy.getId());
                    result.setData(wo);
                }
            }
            emc.commit();

            if (BooleanUtils.isTrue(wi.getIsGrantImmediately())) {
                AttendanceV2LeavePolicy p = emc.find(result.getData().getId(),
                        AttendanceV2LeavePolicy.class);
                QueueAttendanceV2LeavePolicyGrantModel model = new QueueAttendanceV2LeavePolicyGrantModel();
                model.setPolicy(p);
                model.setIsImmediately(true);
                ThisApplication.queueV2LeavePolicyGrant.send(model);
            }

            return result;
        }

    }

    private static final Pattern EXPIRE_MONTH_DAY_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])-([0-2][0-9]|3[0-1])$");

    private boolean isValidExpireMonthDay(String expireMonthDay) {
        if (!EXPIRE_MONTH_DAY_PATTERN.matcher(expireMonthDay).matches()) {
            return false;
        }
        String[] monthDay = expireMonthDay.split("-");
        int month = Integer.parseInt(monthDay[0]);
        int day = Integer.parseInt(monthDay[1]);
        return YearMonth.of(2001, month).isValidDay(day);
    }

    // 解析人员列表，把组织下人员都查询出来 然后比较
    private boolean checkGrantScopeListHasSamePerson(List<String> scopeList1,
            List<String> excludeList1, List<String> scopeList2, List<String> excludeList2,
            Business business)
            throws Exception {
        List<String> userList1 = new ArrayList<>();
        for (String s : scopeList1) {
            drillDownPerson(userList1, s, business);
        }
        List<String> userList2 = new ArrayList<>();
        for (String s2 : scopeList2) {
            drillDownPerson(userList2, s2, business);
        }
        List<String> eList1 = Optional.ofNullable(excludeList1).orElse(new ArrayList<>());
        List<String> eList2 = Optional.ofNullable(excludeList2).orElse(new ArrayList<>());
        userList1.removeAll(eList1);
        userList2.removeAll(eList2);
        // 比较两个用户列表是否有重复的数据
        if (userList1.isEmpty() || userList2.isEmpty()) {
            return false;
        }
        java.util.Set<String> set = new java.util.HashSet<>(userList1);
        java.util.Set<String> set2 = new java.util.HashSet<>(userList2);
        for (String u : set2) {
            if (set.contains(u)) {
                return true;
            }
        }
        return false;
    }

    // 解析人员列表，把组织下人员都查询出来放入 userList 中
    private void drillDownPerson(List<String> userList, String filter, Business business)
            throws Exception {
        if (filter.endsWith("@U")) { // 组织转化成人员列表 递归
            List<String> users = business.organization().person().listWithUnitSubNested(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    public static class Wi extends AttendanceV2LeavePolicy {

        private static final long serialVersionUID = -2609308371335390023L;
        static WrapCopier<Wi, AttendanceV2LeavePolicy> copier = WrapCopierFactory.wi(Wi.class,
                AttendanceV2LeavePolicy.class, null,
                JpaObject.FieldsUnmodify);

        @FieldDescribe("是否立即发放")
        private Boolean isGrantImmediately;

        public Boolean getIsGrantImmediately() {
            return isGrantImmediately;
        }

        public void setIsGrantImmediately(Boolean isGrantImmediately) {
            this.isGrantImmediately = isGrantImmediately;
        }


    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -6628188676802865564L;
    }
}
