package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeavePolicyEnums.GrantTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeavePolicy;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.DateTools;

abstract class BaseAction extends StandardJaxrsAction {

    // 根据当前配置计算下次发放时间
    protected void calculateNextExecutionTimeForLeavePolicy(AttendanceV2LeavePolicy policy) throws Exception {
        if (GrantTypeEnum.YEARLY.getValue().equals(policy.getGrantType())) {
            // 按年发放，计算下次发放时间
            String grantTypeValue = policy.getGrantTypeValue();
            if (grantTypeValue == null || !grantTypeValue.startsWith("Y:")) { // Y:后面跟的配置的月份日期格式 01-01
                throw new ExceptionWithMessage("按年发放的发放方式日期规则配置错误");
            }
            String dateStr = grantTypeValue.substring(2); // 验证 dateStr 是否符合 MM-dd 格式
            if (!isValidDate(dateStr, "MM-dd")) {
                throw new ExceptionWithMessage("按年发放的发放方式日期规则配置错误，日期格式应该为 MM-dd");
            }
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            int year = calendar.get(Calendar.YEAR);
            String nextExecutionDateStr = (year + 1 ) + "-" + dateStr; // 明年
            policy.setGrantNextExecuteTime(nextExecutionDateStr);
        } else if (GrantTypeEnum.MONTHLY.getValue().equals(policy.getGrantType())) {
            // 按月发放，计算下次发放时间
            String grantTypeValue = policy.getGrantTypeValue();
            if (grantTypeValue == null || (!grantTypeValue.startsWith("MS:") && !grantTypeValue.startsWith("ME:"))) { // MS:从头数第几天，ME:每月从后倒数第几天
                throw new ExceptionWithMessage("按月发放的发放方式日期规则配置错误");
            }
            String dayStr = grantTypeValue.substring(3); // 验证 dayStr 是否为数字
            if (!Pattern.matches("\\d+", dayStr)) {
                throw new ExceptionWithMessage("按月发放的发放方式日期规则配置错误，天数应该为数字");
            }
            int day = Integer.parseInt(dayStr);
            if (grantTypeValue.startsWith("MS:")) {
                Date now = new Date();
                Date bizDate = nextMonthStartDate(now, day);
                policy.setGrantNextExecuteTime(DateTools.formatDate(bizDate));
            } else if (grantTypeValue.startsWith("ME:")) {
                Date now = new Date();
                Date bizDate = nextMonthEndDate(now, day);
                policy.setGrantNextExecuteTime(DateTools.formatDate(bizDate));
            }
        }  
    }


    private Boolean isValidDate(String str, String format) {
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
    private Date nextMonthStartDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
    // 计算下个月的倒数第几天的日期
    private Date nextMonthEndDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, -day + 1);
        return calendar.getTime();
    }
}
