package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.base.core.project.tools.DateTools;

public class AttendanceV2LeaveManager {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceV2LeaveManager.class);

    public static final String ONE_TIME = "ONE_TIME"; // 一次性


    /**
     * 异步更新假期账户余额数据
     *
     * @param personDN
     * @param leaveTypeId
     */
    public static void asyncUpdateLeaveAccount(String personDN, String leaveTypeId) {
        if (logger.isDebugEnabled()) {
            logger.debug("准备异步更新用户 {} 的请假类型 {} 的余额数据", personDN, leaveTypeId);
        }
        Thread thread = new Thread(() -> {
            try {
                updateLeaveAccount(personDN, leaveTypeId);
            } catch (Exception e) {
                logger.error(e);
            }

        });
        thread.start();
    }

    public static void updateLeaveAccount(String personDN, String leaveTypeId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<AttendanceV2LeaveLedger> ledgers = emc.listEqualAndEqualAndEqual(AttendanceV2LeaveLedger.class,
                    AttendanceV2LeaveLedger.person_FIELDNAME, personDN,
                    AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME, leaveTypeId,
                    AttendanceV2LeaveLedger.active_FIELDNAME, true);
            List<AttendanceV2LeaveAccount> accounts = emc.listEqualAndEqual(AttendanceV2LeaveAccount.class,
                    AttendanceV2LeaveAccount.person_FIELDNAME, personDN,
                    AttendanceV2LeaveAccount.leaveTypeId_FIELDNAME, leaveTypeId);
            AttendanceV2LeaveAccount account = null;
            if (accounts == null || accounts.isEmpty()) {
                account = new AttendanceV2LeaveAccount();
                account.setPerson(personDN);
                account.setLeaveTypeId(leaveTypeId);
                emc.beginTransaction(AttendanceV2LeaveAccount.class);
                emc.persist(account, CheckPersistType.all);
                emc.commit();
            } else {
                account = accounts.get(0);
            }

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
            account.setTotalGranted(totalGranted);
            account.setTotalUsed(totalUsed);
            account.setBalance(balance);
            emc.beginTransaction(AttendanceV2LeaveAccount.class);
            AttendanceV2LeaveAccount old = emc.find(account.getId(), AttendanceV2LeaveAccount.class);
            account.copyTo(old, JpaObject.FieldsUnmodify);
            emc.check(old, CheckPersistType.all);
            emc.commit();
        }
    }


     // 验证日期字符串是否符合指定格式
    private static Boolean isValidDate(String str, String format) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(format)) {
            return Boolean.FALSE;
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(str);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    // 计算下个月的某一天的日期
    private static Date nextMonthStartDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    // 计算下个月的倒数第几天的日期
    private static Date nextMonthEndDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, -day + 1);
        return calendar.getTime();
    }
}
