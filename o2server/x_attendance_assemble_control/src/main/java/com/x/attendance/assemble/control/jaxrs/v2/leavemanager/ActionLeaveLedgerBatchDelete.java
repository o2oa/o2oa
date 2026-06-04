package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionLeaveLedgerBatchDelete extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }

            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            validateParameters(wi);
            List<AttendanceV2LeaveLedger> ledgers = emc.listEqualAndEqual(AttendanceV2LeaveLedger.class,
                    AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME, wi.getLeaveTypeId(),
                    AttendanceV2LeaveLedger.grantPeriod_FIELDNAME, wi.getGrantPeriod());

            int deletedRows = 0;
            int transactionRows = 0;
            Set<String> persons = new HashSet<>();
            if (ledgers != null && !ledgers.isEmpty()) {
                emc.beginTransaction(AttendanceV2LeaveLedger.class);
                emc.beginTransaction(AttendanceV2LeaveTransaction.class);
                for (AttendanceV2LeaveLedger ledger : ledgers) {
                    AttendanceV2LeaveTransaction transaction = buildCancelTransaction(ledger);
                    emc.persist(transaction, CheckPersistType.all);
                    emc.remove(ledger);
                    persons.add(ledger.getPerson());
                    transactionRows++;
                    deletedRows++;
                }
                emc.commit();
            }

            int accountRows = 0;
            for (String person : persons) {
                AttendanceV2LeaveManager.updateLeaveAccount(person, wi.getLeaveTypeId());
                accountRows++;
            }

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setDeletedRows(deletedRows);
            wo.setTransactionRows(transactionRows);
            wo.setAccountRows(accountRows);
            result.setData(wo);
            return result;
        }
    }

    static void validateParameters(Wi wi) throws Exception {
        if (wi == null || StringUtils.isBlank(wi.getLeaveTypeId())) {
            throw new ExceptionEmptyParameter("假期类型ID");
        }
        if (StringUtils.isBlank(wi.getGrantPeriod())) {
            throw new ExceptionEmptyParameter("发放周期");
        }
    }

    static AttendanceV2LeaveTransaction buildCancelTransaction(AttendanceV2LeaveLedger ledger) {
        AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
        transaction.setPerson(ledger.getPerson());
        transaction.setLeaveTypeId(ledger.getLeaveTypeId());
        transaction.setLedgerId(ledger.getId());
        transaction.setBizType(BizTypeEnum.CANCEL.getValue());
        transaction.setAmount(ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0);
        return transaction;
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -1758785747192285855L;

        @FieldDescribe("假期类型ID")
        private String leaveTypeId;

        @FieldDescribe("发放周期")
        private String grantPeriod;

        public String getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(String leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public String getGrantPeriod() {
            return grantPeriod;
        }

        public void setGrantPeriod(String grantPeriod) {
            this.grantPeriod = grantPeriod;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 3245191947421139026L;

        @FieldDescribe("删除的批次数量")
        private int deletedRows;

        @FieldDescribe("创建的流水数量")
        private int transactionRows;

        @FieldDescribe("更新的账户数量")
        private int accountRows;

        public int getDeletedRows() {
            return deletedRows;
        }

        public void setDeletedRows(int deletedRows) {
            this.deletedRows = deletedRows;
        }

        public int getTransactionRows() {
            return transactionRows;
        }

        public void setTransactionRows(int transactionRows) {
            this.transactionRows = transactionRows;
        }

        public int getAccountRows() {
            return accountRows;
        }

        public void setAccountRows(int accountRows) {
            this.accountRows = accountRows;
        }
    }
}
