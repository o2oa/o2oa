package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger_;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.Person;

public class ActionLeaveLedgerAdjust extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }

            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            boolean adjustedAmountPresent = hasField(jsonElement, "adjustedAmount");
            validateParameters(wi, adjustedAmountPresent);

            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("人员 " + wi.getPerson());
            }
            AttendanceV2LeaveType leaveType = emc.find(wi.getLeaveTypeId(), AttendanceV2LeaveType.class);
            if (leaveType == null) {
                throw new ExceptionNotExistObject("请假类型 " + wi.getLeaveTypeId());
            }

            String personDN = person.getDistinguishedName();
            List<AttendanceV2LeaveLedger> ledgers = listTargetLedgers(emc, wi, personDN);
            boolean expireTimePresent = hasField(jsonElement, AttendanceV2LeaveLedger.expireTime_FIELDNAME);

            emc.beginTransaction(AttendanceV2LeaveLedger.class);
            emc.beginTransaction(AttendanceV2LeaveTransaction.class);
            Adjustment adjustment;
            if (ledgers == null || ledgers.isEmpty()) {
                AttendanceV2LeaveLedger ledger = buildAdjustmentLedger(personDN, wi, new Date());
                adjustment = buildNewLedgerAdjustment(ledger, wi.getAdjustedAmount());
                emc.persist(ledger, CheckPersistType.all);
            } else {
                adjustment = applyAdjustment(ledgers, wi.getAdjustedAmount(), wi.getExpireTime(),
                        expireTimePresent);
                for (AttendanceV2LeaveLedger ledger : ledgers) {
                    emc.check(ledger, CheckPersistType.all);
                }
            }
            for (AttendanceV2LeaveTransaction transaction : adjustment.getTransactions()) {
                emc.persist(transaction, CheckPersistType.all);
            }
            emc.commit();

            AttendanceV2LeaveManager.updateLeaveAccount(personDN, wi.getLeaveTypeId());

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setBeforeAmount(adjustment.getBeforeAmount());
            wo.setAdjustedAmount(adjustment.getAdjustedAmount());
            wo.setAdjustAmount(adjustment.getAdjustAmount());
            result.setData(wo);
            return result;
        }
    }

    static void validateParameters(Wi wi, boolean adjustedAmountPresent) throws Exception {
        if (wi == null || StringUtils.isBlank(wi.getPerson())) {
            throw new ExceptionEmptyParameter("用户");
        }
        if (StringUtils.isBlank(wi.getLeaveTypeId())) {
            throw new ExceptionEmptyParameter("假期类型ID");
        }
        if (!adjustedAmountPresent || wi.getAdjustedAmount() == null) {
            throw new ExceptionEmptyParameter("调整后的额度");
        }
        if (wi.getAdjustedAmount() < 0) {
            throw new ExceptionWithMessage("调整后的额度不能小于0");
        }
    }

    static AttendanceV2LeaveLedger buildAdjustmentLedger(String personDN, Wi wi, Date now) {
        AttendanceV2LeaveLedger ledger = new AttendanceV2LeaveLedger();
        ledger.setPerson(personDN);
        ledger.setLeaveTypeId(wi.getLeaveTypeId());
        ledger.setGrantPeriod("ADJUST_" + now.getTime());
        ledger.setGrantAmount(wi.getAdjustedAmount());
        ledger.setUsedAmount(0.0);
        ledger.setRemainingAmount(wi.getAdjustedAmount());
        ledger.setGrantTime(now);
        ledger.setExpireTime(wi.getExpireTime());
        ledger.setActive(true);
        return ledger;
    }

    static Adjustment buildNewLedgerAdjustment(AttendanceV2LeaveLedger ledger, Double adjustedAmount) {
        return new Adjustment(0.0, adjustedAmount, adjustedAmount,
                java.util.Collections.singletonList(buildAdjustTransaction(ledger, adjustedAmount)));
    }

    static Adjustment applyAdjustment(List<AttendanceV2LeaveLedger> ledgers, Double adjustedAmount, Date expireTime,
            boolean expireTimePresent) throws Exception {
        double beforeAmount = totalRemainingAmount(ledgers);
        double adjustAmount = adjustedAmount - beforeAmount;
        List<AttendanceV2LeaveTransaction> transactions;
        if (adjustAmount > 0) {
            addBalanceToLatestLedger(ledgers.get(0), adjustAmount, expireTime, expireTimePresent);
            transactions = java.util.Collections.singletonList(buildAdjustTransaction(ledgers.get(0), adjustAmount));
        } else if (adjustAmount < 0) {
            transactions = deductBalanceFromLedgers(ledgers, -adjustAmount, expireTime, expireTimePresent);
        } else if (expireTimePresent) {
            ledgers.get(0).setExpireTime(expireTime);
            transactions = java.util.Collections.singletonList(buildAdjustTransaction(ledgers.get(0), 0.0));
        } else {
            transactions = java.util.Collections.singletonList(buildAdjustTransaction(ledgers.get(0), 0.0));
        }
        return new Adjustment(beforeAmount, adjustedAmount, adjustAmount, transactions);
    }

    private static void addBalanceToLatestLedger(AttendanceV2LeaveLedger ledger, double amount, Date expireTime,
            boolean expireTimePresent) {
        double remainingAmount = (ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0) + amount;
        resetLedgerAmount(ledger, remainingAmount);
        if (expireTimePresent) {
            ledger.setExpireTime(expireTime);
        }
    }

    private static List<AttendanceV2LeaveTransaction> deductBalanceFromLedgers(List<AttendanceV2LeaveLedger> ledgers,
            double amount, Date expireTime, boolean expireTimePresent) throws Exception {
        java.util.ArrayList<AttendanceV2LeaveTransaction> transactions = new java.util.ArrayList<>();
        double remainingDeductAmount = amount;
        for (int i = 0; i < ledgers.size(); i++) {
            AttendanceV2LeaveLedger ledger = ledgers.get(i);
            if (i == 0 && expireTimePresent) {
                ledger.setExpireTime(expireTime);
            }
            if (remainingDeductAmount <= 0) {
                break;
            }
            double remainingAmount = ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            if (remainingAmount <= 0) {
                continue;
            }
            double deductAmount = Math.min(remainingAmount, remainingDeductAmount);
            resetLedgerAmount(ledger, remainingAmount - deductAmount);
            transactions.add(buildAdjustTransaction(ledger, -deductAmount));
            remainingDeductAmount -= deductAmount;
        }
        if (remainingDeductAmount > 0) {
            throw new ExceptionWithMessage("调减额度不能超过当前剩余额度");
        }
        return transactions;
    }

    private static void resetLedgerAmount(AttendanceV2LeaveLedger ledger, double remainingAmount) {
        double usedAmount = ledger.getUsedAmount() != null ? ledger.getUsedAmount() : 0.0;
        ledger.setGrantAmount(usedAmount + remainingAmount);
        ledger.setRemainingAmount(remainingAmount);
    }

    static AttendanceV2LeaveTransaction buildAdjustTransaction(AttendanceV2LeaveLedger ledger, Double adjustAmount) {
        AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
        transaction.setPerson(ledger.getPerson());
        transaction.setLeaveTypeId(ledger.getLeaveTypeId());
        transaction.setLedgerId(ledger.getId());
        transaction.setBizType(BizTypeEnum.ADJUST.getValue());
        transaction.setAmount(adjustAmount);
        return transaction;
    }

    private static double totalRemainingAmount(List<AttendanceV2LeaveLedger> ledgers) {
        double total = 0.0;
        if (ledgers != null) {
            for (AttendanceV2LeaveLedger ledger : ledgers) {
                total += ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            }
        }
        return total;
    }

    private List<AttendanceV2LeaveLedger> listTargetLedgers(EntityManagerContainer emc, Wi wi, String personDN)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveLedger.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveLedger> cq = cb.createQuery(AttendanceV2LeaveLedger.class);
        Root<AttendanceV2LeaveLedger> root = cq.from(AttendanceV2LeaveLedger.class);
        Predicate p = cb.equal(root.get(AttendanceV2LeaveLedger_.person), personDN);
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveLedger_.leaveTypeId), wi.getLeaveTypeId()));
        p = cb.and(p, cb.equal(root.get(AttendanceV2LeaveLedger_.active), true));
        cq.select(root).where(p).orderBy(cb.desc(root.<Date>get(AttendanceV2LeaveLedger.grantTime_FIELDNAME)),
                cb.desc(root.get(AttendanceV2LeaveLedger_.id)));
        return em.createQuery(cq).getResultList();
    }

    private static boolean hasField(JsonElement jsonElement, String fieldName) {
        return jsonElement != null && jsonElement.isJsonObject()
                && jsonElement.getAsJsonObject().has(fieldName);
    }

    static class Adjustment {

        private final Double beforeAmount;
        private final Double adjustedAmount;
        private final Double adjustAmount;
        private final List<AttendanceV2LeaveTransaction> transactions;

        Adjustment(Double beforeAmount, Double adjustedAmount, Double adjustAmount,
                List<AttendanceV2LeaveTransaction> transactions) {
            this.beforeAmount = beforeAmount;
            this.adjustedAmount = adjustedAmount;
            this.adjustAmount = adjustAmount;
            this.transactions = transactions;
        }

        public Double getBeforeAmount() {
            return beforeAmount;
        }

        public Double getAdjustedAmount() {
            return adjustedAmount;
        }

        public Double getAdjustAmount() {
            return adjustAmount;
        }

        public List<AttendanceV2LeaveTransaction> getTransactions() {
            return transactions;
        }
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 8335039692968182186L;

        @FieldDescribe("用户")
        private String person;

        @FieldDescribe("假期类型ID")
        private String leaveTypeId;

        @FieldDescribe("调整后的账户剩余额度")
        private Double adjustedAmount;

        @FieldDescribe("过期时间")
        private Date expireTime;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(String leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public Double getAdjustedAmount() {
            return adjustedAmount;
        }

        public void setAdjustedAmount(Double adjustedAmount) {
            this.adjustedAmount = adjustedAmount;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -5825139547241453472L;

        @FieldDescribe("调整前额度")
        private Double beforeAmount;

        @FieldDescribe("调整后额度")
        private Double adjustedAmount;

        @FieldDescribe("调整差额")
        private Double adjustAmount;

        public Double getBeforeAmount() {
            return beforeAmount;
        }

        public void setBeforeAmount(Double beforeAmount) {
            this.beforeAmount = beforeAmount;
        }

        public Double getAdjustedAmount() {
            return adjustedAmount;
        }

        public void setAdjustedAmount(Double adjustedAmount) {
            this.adjustedAmount = adjustedAmount;
        }

        public Double getAdjustAmount() {
            return adjustAmount;
        }

        public void setAdjustAmount(Double adjustAmount) {
            this.adjustAmount = adjustAmount;
        }
    }
}
