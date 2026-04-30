package com.x.attendance.assemble.control.schedule.v2;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;

import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;

public class AttendanceV2LeaveLedgerExpireTask extends AbstractJob {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2LeaveLedgerExpireTask.class);

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤假期额度过期定时器开始执行==============================");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Date now = new Date();
            List<AttendanceV2LeaveLedger> ledgers = listExpiredLedgers(emc, now);
            Set<AccountKey> accountKeys = new HashSet<>();
            for (AttendanceV2LeaveLedger ledger : ledgers) {
                try {
                    if (expireLedger(emc, ledger, now)) {
                        accountKeys.add(new AccountKey(ledger.getPerson(), ledger.getLeaveTypeId()));
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            for (AccountKey accountKey : accountKeys) {
                try {
                    refreshLeaveAccount(emc, accountKey);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            logger.info("新版考勤假期额度过期处理完成，过期批次数量: {}, 更新账户数量: {}.", ledgers.size(), accountKeys.size());
        } catch (Exception e) {
            logger.error(e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("======================新版考勤假期额度过期定时器执行完成==============================");
        }
    }

    private List<AttendanceV2LeaveLedger> listExpiredLedgers(EntityManagerContainer emc, Date now) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveLedger.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveLedger> cq = cb.createQuery(AttendanceV2LeaveLedger.class);
        Root<AttendanceV2LeaveLedger> root = cq.from(AttendanceV2LeaveLedger.class);
        Predicate p = cb.equal(root.<Boolean>get(AttendanceV2LeaveLedger.active_FIELDNAME), true);
        p = cb.and(p, cb.isNotNull(root.get(AttendanceV2LeaveLedger.expireTime_FIELDNAME)));
        p = cb.and(p, cb.lessThanOrEqualTo(root.<Date>get(AttendanceV2LeaveLedger.expireTime_FIELDNAME), now));
        cq.select(root).where(p).orderBy(cb.asc(root.<Date>get(AttendanceV2LeaveLedger.expireTime_FIELDNAME)));
        return em.createQuery(cq).getResultList();
    }

    private boolean expireLedger(EntityManagerContainer emc, AttendanceV2LeaveLedger ledger, Date now) throws Exception {
        emc.beginTransaction(AttendanceV2LeaveLedger.class);
        emc.beginTransaction(AttendanceV2LeaveTransaction.class);
        AttendanceV2LeaveLedger old = emc.find(ledger.getId(), AttendanceV2LeaveLedger.class);
        if (old == null || !Boolean.TRUE.equals(old.getActive()) || old.getExpireTime() == null
                || old.getExpireTime().after(now)) {
            emc.commit();
            return false;
        }
        Double expireAmount = old.getRemainingAmount() != null ? old.getRemainingAmount() : 0.0;
        old.setActive(false);
        emc.check(old, CheckPersistType.all);

        AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
        transaction.setPerson(old.getPerson());
        transaction.setLeaveTypeId(old.getLeaveTypeId());
        transaction.setLedgerId(old.getId());
        transaction.setBizType(BizTypeEnum.EXPIRE.getValue());
        transaction.setAmount(expireAmount);
        emc.persist(transaction, CheckPersistType.all);
        emc.commit();
        return true;
    }

    private void refreshLeaveAccount(EntityManagerContainer emc, AccountKey accountKey) throws Exception {
        List<AttendanceV2LeaveLedger> ledgers = emc.listEqualAndEqualAndEqual(AttendanceV2LeaveLedger.class,
                AttendanceV2LeaveLedger.person_FIELDNAME, accountKey.person,
                AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME, accountKey.leaveTypeId,
                AttendanceV2LeaveLedger.active_FIELDNAME, true);
        double totalGranted = 0.0;
        double totalUsed = 0.0;
        double balance = 0.0;
        if (ledgers != null && !ledgers.isEmpty()) {
            for (AttendanceV2LeaveLedger ledger : ledgers) {
                totalGranted += ledger.getGrantAmount() != null ? ledger.getGrantAmount() : 0.0;
                totalUsed += ledger.getUsedAmount() != null ? ledger.getUsedAmount() : 0.0;
                balance += ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            }
        }
        List<AttendanceV2LeaveAccount> accounts = emc.listEqualAndEqual(AttendanceV2LeaveAccount.class,
                AttendanceV2LeaveAccount.person_FIELDNAME, accountKey.person,
                AttendanceV2LeaveAccount.leaveTypeId_FIELDNAME, accountKey.leaveTypeId);
        emc.beginTransaction(AttendanceV2LeaveAccount.class);
        if (accounts == null || accounts.isEmpty()) {
            AttendanceV2LeaveAccount account = new AttendanceV2LeaveAccount();
            account.setPerson(accountKey.person);
            account.setLeaveTypeId(accountKey.leaveTypeId);
            account.setTotalGranted(totalGranted);
            account.setTotalUsed(totalUsed);
            account.setBalance(balance);
            emc.persist(account, CheckPersistType.all);
        } else {
            AttendanceV2LeaveAccount old = emc.find(accounts.get(0).getId(), AttendanceV2LeaveAccount.class);
            old.setTotalGranted(totalGranted);
            old.setTotalUsed(totalUsed);
            old.setBalance(balance);
            emc.check(old, CheckPersistType.all);
        }
        emc.commit();
    }

    private static class AccountKey {

        private final String person;
        private final String leaveTypeId;

        private AccountKey(String person, String leaveTypeId) {
            this.person = person;
            this.leaveTypeId = leaveTypeId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(person, leaveTypeId);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof AccountKey)) {
                return false;
            }
            AccountKey other = (AccountKey) obj;
            return Objects.equals(person, other.person) && Objects.equals(leaveTypeId, other.leaveTypeId);
        }
    }
}
