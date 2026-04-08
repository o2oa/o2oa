package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.ExpireTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantScopeTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantTypeEnum;
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

public class ActionLeavePolicyPost extends BaseAction {

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
            List<AttendanceV2LeavePolicy> checkRepetitive = emc.listEqualAndEqual(AttendanceV2LeavePolicy.class,
                    AttendanceV2LeavePolicy.policyName_FIELDNAME, wi.getPolicyName(), AttendanceV2LeavePolicy.active_FIELDNAME, true);
            if (checkRepetitive != null && !checkRepetitive.isEmpty()) {
                for (AttendanceV2LeavePolicy check : checkRepetitive) {
                    if (check.getPolicyName().equals(wi.getPolicyName()) && !check.getId().equals(wi.getId())) {
                        throw new ExceptionWithMessage("规则名称已存在");
                    }
                }
            }
            if (StringUtils.isBlank(wi.getLeaveTypeId())) {
                throw new ExceptionEmptyParameter("假期类型ID");
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
            // 全员 校验是否有同类型的了 ， todo： GrantScopeTypeEnum.DEPARTMENT下是否判断重复
            if (GrantScopeTypeEnum.ALL.getValue().equals(wi.getGrantScopeType())) {
                List<AttendanceV2LeavePolicy> checkTypeAndScopeAll = emc.listEqualAndEqualAndEqual(
                        AttendanceV2LeavePolicy.class, AttendanceV2LeavePolicy.leaveTypeId_FIELDNAME,
                        wi.getLeaveTypeId(), AttendanceV2LeavePolicy.grantScopeType_FIELDNAME,
                        GrantScopeTypeEnum.ALL.getValue(), AttendanceV2LeavePolicy.active_FIELDNAME, true);
                if (checkTypeAndScopeAll != null && !checkTypeAndScopeAll.isEmpty()) {
                    for (AttendanceV2LeavePolicy check : checkRepetitive) {
                        if (!check.getId().equals(wi.getId())) {
                            throw new ExceptionWithMessage("当前假期类型已有配置规则");
                        }
                    }
                }
            }
            if (StringUtils.isBlank(wi.getGrantType()) || (!GrantTypeEnum.YEARLY.getValue().equals(wi.getGrantType())
                    && !GrantTypeEnum.MONTHLY.getValue().equals(wi.getGrantType())
                    && !GrantTypeEnum.ONE_TIME.getValue().equals(wi.getGrantType()))) {
                throw new ExceptionEmptyParameter("发放方式");
            }
            if (StringUtils.isBlank(wi.getExpireType()) || (!ExpireTypeEnum.NEVER.getValue().equals(wi.getExpireType())
                    && !ExpireTypeEnum.FIXED.getValue().equals(wi.getExpireType())
                    && !ExpireTypeEnum.RELATIVE.getValue().equals(wi.getExpireType()))) {
                throw new ExceptionEmptyParameter("过期类型");
            }
            // 单次 发放必须指定发放额度，且不能为负数
            if ((GrantTypeEnum.ONE_TIME.getValue().equals(wi.getGrantType())
                    || GrantTypeEnum.MONTHLY.getValue().equals(wi.getGrantType()))
                    && (wi.getGrantAmount() == null || wi.getGrantAmount() < 0)) {
                throw new ExceptionEmptyParameter("发放额度");
            } else if (GrantTypeEnum.YEARLY.getValue().equals(wi.getGrantType())) {
                if (wi.getGrantAmountType() == null || !wi.getGrantAmountType().validate()) {
                    throw new ExceptionWithMessage("发放额度规则错误");
                }
            }
            if (BooleanUtils.isTrue(wi.getCarryForward())
                    && (wi.getMaxCarryForward() == null || wi.getMaxCarryForward() < 0)) {
                throw new ExceptionEmptyParameter("最大结转额度");
            }

            AttendanceV2LeavePolicy leavePolicy = Wi.copier.copy(wi);
            // 生成grantNextExecuteTime
            AttendanceV2LeaveManager.calculateNextExecutionTimeForLeavePolicy(leavePolicy);
            emc.beginTransaction(AttendanceV2LeavePolicy.class);
            if (StringUtils.isBlank(leavePolicy.getId())) {
                emc.persist(leavePolicy, CheckPersistType.all);
                Wo wo = new Wo();
                wo.setId(leavePolicy.getId());
                result.setData(wo);
            } else {
                AttendanceV2LeavePolicy old = emc.find(leavePolicy.getId(), AttendanceV2LeavePolicy.class);
                if (old != null) {
                    leavePolicy.copyTo(old, JpaObject.FieldsUnmodify);
                    emc.check(old, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(old.getId());
                    result.setData(wo);
                } else {
                    emc.persist(leavePolicy, CheckPersistType.all);
                    Wo wo = new Wo();
                    wo.setId(leavePolicy.getId());
                    result.setData(wo);
                }
            }
            emc.commit();

            if (BooleanUtils.isTrue(wi.getIsGrantImmediately())) {
                AttendanceV2LeavePolicy p = emc.find(result.getData().getId(), AttendanceV2LeavePolicy.class);
                QueueAttendanceV2LeavePolicyGrantModel model = new QueueAttendanceV2LeavePolicyGrantModel();
                model.setPolicy(p);
                model.setIsImmediately(true);
                ThisApplication.queueV2LeavePolicyGrant.send(model);
            }

            return result;
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
