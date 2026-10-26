package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTransactionEnums.BizTypeEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

abstract class BaseAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger(BaseAction.class);



    /**
     * 扣除请假余额
     * 
     * @param leaveType
     * @param personDN
     * @param useAmount
     * @throws Exception
     */
    protected void deductingLeaveBalance(AttendanceV2LeaveType leaveType, String personDN, Double useAmount,
            String leaveRequestId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            emc.beginTransaction(AttendanceV2LeaveLedger.class);
            emc.beginTransaction(AttendanceV2LeaveTransaction.class);
            deductingLeaveBalance(emc, leaveType, personDN, useAmount, leaveRequestId);
            emc.commit();
            AttendanceV2LeaveManager.asyncUpdateLeaveAccount(personDN, leaveType.getId());
        }
    }

    protected void deductingLeaveBalance(EntityManagerContainer emc, AttendanceV2LeaveType leaveType, String personDN,
            Double useAmount, String leaveRequestId) throws Exception {
        if (QuotaTypeEnum.UNLIMITED.getValue().equals(leaveType.getQuotaType())) {
            logger.warn("请假类型 {} 的额度类型是 {}，不扣减余额。", leaveType.getName(), leaveType.getQuotaType());
            return; // 不扣减余额
        }
        List<AttendanceV2LeaveLedger> ledgers = emc.listEqualAndEqualAndEqual(AttendanceV2LeaveLedger.class,
                AttendanceV2LeaveLedger.person_FIELDNAME, personDN, AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME,
                leaveType.getId(), AttendanceV2LeaveLedger.active_FIELDNAME, true);
        if (ledgers == null || ledgers.isEmpty()) {
            throw new ExceptionWithMessage("没有找到用户 " + personDN + " 的请假类型 " + leaveType.getName() + " 的假期余额记录");
        }
        ledgers.sort(new Comparator<AttendanceV2LeaveLedger>() {
            @Override
            public int compare(AttendanceV2LeaveLedger a, AttendanceV2LeaveLedger b) {
                boolean aOne = AttendanceV2LeaveManager.ONE_TIME.equals(a.getGrantPeriod());
                boolean bOne = AttendanceV2LeaveManager.ONE_TIME.equals(b.getGrantPeriod());
                // ONE_TIME 放最后
                if (aOne != bOne) {
                    return aOne ? 1 : -1;
                }
                // 正常时间排序
                return grantPeriodParse(a.getGrantPeriod()).compareTo(grantPeriodParse(b.getGrantPeriod()));
            }
        });
        // AttendanceV2LeaveLedger中的remainingAmount是剩余额度，
        // 根据传入的useAmount来扣减余额，扣减规则是按照发放时间顺序来扣减，先扣减最早发放的额度。
        // 如果第一条不够扣减完，就继续扣减第二条，直到扣减完或者没有余额了。扣减过程中要更新每条记录的remainingAmount字段。
        double totalRemainingAmount = 0.0;
        for (AttendanceV2LeaveLedger ledger : ledgers) {
            double remaining = ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            totalRemainingAmount += remaining;
        }
        if (totalRemainingAmount < useAmount) {
            throw new ExceptionWithMessage(
                    "用户 " + personDN + " 的请假类型 " + leaveType.getName() + " 的假期余额不足，无法扣减 " + useAmount + " 天");
        }
        for (AttendanceV2LeaveLedger ledger : ledgers) {
            if (useAmount <= 0) {
                break; // 扣减完了
            }
            double remaining = ledger.getRemainingAmount() != null ? ledger.getRemainingAmount() : 0.0;
            if (remaining <= 0) {
                continue; // 没有余额了，继续扣减下一条
            }
            double indexUseAmount = 0.0;
            if (remaining >= useAmount) {
                indexUseAmount = useAmount;
                // 余额足够扣减完
                ledger.setRemainingAmount(remaining - useAmount);
                useAmount = 0.0;
            } else {
                indexUseAmount = remaining;
                // 余额不够扣减完，扣减剩余的余额，继续扣减下一条
                ledger.setRemainingAmount(0.0);
                useAmount -= remaining;
            }
            ledger.setUsedAmount(indexUseAmount + (ledger.getUsedAmount() != null ? ledger.getUsedAmount() : 0.0));
            AttendanceV2LeaveLedger old = emc.find(ledger.getId(), AttendanceV2LeaveLedger.class);
            ledger.copyTo(old, JpaObject.FieldsUnmodify);
            emc.check(old, CheckPersistType.all);
            AttendanceV2LeaveTransaction transaction = new AttendanceV2LeaveTransaction();
            transaction.setPerson(personDN);
            transaction.setLeaveTypeId(leaveType.getId());
            transaction.setLedgerId(ledger.getId());
            if (StringUtils.isNotBlank(leaveRequestId)) {
                transaction.setLeaveRequestId(leaveRequestId);
            }
            transaction.setBizType(BizTypeEnum.USE.getValue());
            transaction.setAmount(indexUseAmount); // 扣除数量
            emc.persist(transaction, CheckPersistType.all);
        }
    }

    // 解析发放周期，ONE_TIME 特例，表示只发放一次，给一个极大值保证它在最后面
    private YearMonth grantPeriodParse(String str) {
        if (AttendanceV2LeaveManager.ONE_TIME.equals(str)) {
            // 给一个极大值，保证它在最后（理论上不会走到这里，但更安全）
            return YearMonth.of(9999, 12);
        }
        if (str.length() == 4) {
            return YearMonth.of(Integer.parseInt(str), 1);
        } else {
            return YearMonth.parse(str);
        }
    }

    // 解析人员列表，把组织下人员都查询出来放入 userList 中 不递归
    protected void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (filter.endsWith("@U")) {
            List<String> users = business.organization().person().listWithUnitSubDirect(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    // 根据请假类型的额度类型来查询请假类型列表
    protected List<AttendanceV2LeaveType> getLeaveTypeList(String quotaType) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceV2LeaveType> types = emc.listEqual(AttendanceV2LeaveType.class,
                    AttendanceV2LeaveType.active_FIELDNAME, true);
            if (StringUtils.isNotBlank(quotaType) && (QuotaTypeEnum.QUOTA.getValue().equals(quotaType)
                    || QuotaTypeEnum.UNLIMITED.getValue().equals(quotaType))) {
                types = types.stream().filter(t -> quotaType.equals(t.getQuotaType())).collect(Collectors.toList());
            }
           //根据AttendanceV2LeaveType的orderNumber字段 倒序排序
            types.sort(Comparator.comparing(AttendanceV2LeaveType::getOrderNumber, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            return types;
        }
    }


   
}
