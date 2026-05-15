package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;

public class ActionLeaveLedgerPost extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson person, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(person)) {
                throw new ExceptionAccessDenied(person);
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            AttendanceV2LeaveLedger ledger = buildLedger(wi, new Date(), hasField(jsonElement,
                    AttendanceV2LeaveLedger.grantAmount_FIELDNAME));
            validateLedgerNotExists(emc.listEqualAndEqualAndEqual(AttendanceV2LeaveLedger.class,
                    AttendanceV2LeaveLedger.grantPeriod_FIELDNAME, ledger.getGrantPeriod(),
                    AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME, ledger.getLeaveTypeId(),
                    AttendanceV2LeaveLedger.person_FIELDNAME, ledger.getPerson()));

            emc.beginTransaction(AttendanceV2LeaveLedger.class);
            emc.persist(ledger, CheckPersistType.all);
            emc.commit();

            addLeaveTransaction(emc, ledger);
            AttendanceV2LeaveManager.asyncUpdateLeaveAccount(ledger.getPerson(), ledger.getLeaveTypeId());

            Wo wo = new Wo();
            wo.setId(ledger.getId());
            result.setData(wo);
            return result;
        }
    }

    static AttendanceV2LeaveLedger buildLedger(Wi wi, Date now, boolean grantAmountPresent)
            throws Exception {
        if (StringUtils.isBlank(wi.getPerson())) {
            throw new ExceptionEmptyParameter("用户");
        }
        if (StringUtils.isBlank(wi.getLeaveTypeId())) {
            throw new ExceptionEmptyParameter("假期类型ID");
        }
        if (StringUtils.isBlank(wi.getGrantPeriod())) {
            throw new ExceptionEmptyParameter("发放周期");
        }
        if (!grantAmountPresent || wi.getGrantAmount() == null) {
            throw new ExceptionEmptyParameter("发放额度");
        }
        if (wi.getGrantAmount() < 0) {
            throw new ExceptionWithMessage("发放额度不能小于0");
        }

        AttendanceV2LeaveLedger ledger = Wi.copier.copy(wi);
        ledger.setUsedAmount(0.0);
        ledger.setRemainingAmount(wi.getGrantAmount());
        ledger.setGrantTime(now);
        ledger.setActive(true);
        return ledger;
    }

    static void validateLedgerNotExists(List<AttendanceV2LeaveLedger> ledgers) throws Exception {
        if (ledgers != null && !ledgers.isEmpty()) {
            throw new ExceptionWithMessage("当前用户该假期类型在当前发放周期已存在发放批次");
        }
    }

    private static boolean hasField(JsonElement jsonElement, String fieldName) {
        return jsonElement != null && jsonElement.isJsonObject()
                && jsonElement.getAsJsonObject().has(fieldName)
                && !jsonElement.getAsJsonObject().get(fieldName).isJsonNull();
    }

    private static void addLeaveTransaction(EntityManagerContainer emc, AttendanceV2LeaveLedger ledger)
            throws Exception {
        emc.beginTransaction(AttendanceV2LeaveTransaction.class);
        AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
        transaction.setPerson(ledger.getPerson());
        transaction.setLeaveTypeId(ledger.getLeaveTypeId());
        transaction.setLedgerId(ledger.getId());
        transaction.setBizType(BizTypeEnum.GRANT.getValue());
        transaction.setAmount(ledger.getGrantAmount());
        emc.persist(transaction, CheckPersistType.all);
        emc.commit();
    }

    public static class Wi extends AttendanceV2LeaveLedger {

        private static final long serialVersionUID = 5684351370514333193L;
        static WrapCopier<Wi, AttendanceV2LeaveLedger> copier = WrapCopierFactory.wi(Wi.class,
                AttendanceV2LeaveLedger.class, null,
                JpaObject.FieldsUnmodify);

    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -5743427129171552930L;
    }
}
