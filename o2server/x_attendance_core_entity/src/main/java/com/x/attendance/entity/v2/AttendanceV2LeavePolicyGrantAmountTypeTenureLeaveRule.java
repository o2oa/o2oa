package com.x.attendance.entity.v2;




import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2LeavePolicyGrantAmountTypeTenureLeaveRule extends JsonProperties {
 
    private static final long serialVersionUID = -7000765123966414738L;

    public AttendanceV2LeavePolicyGrantAmountTypeTenureLeaveRule(Double minYears, Double maxYears, Double amount) {
        if (minYears == null && maxYears == null) {
            throw new IllegalArgumentException("minYears 和 maxYears 不能同时为 null");
        }
        if (minYears != null && maxYears != null && minYears >= maxYears) {
            throw new IllegalArgumentException("minYears 必须小于 maxYears");
        }
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("amount 必须为非负数");
        }
        this.minYears = minYears;
        this.maxYears = maxYears;
        this.amount = amount;
    }

    @FieldDescribe("最小司龄（包含）, null 表示无下限")
    private Double minYears;

    @FieldDescribe("最大司龄（不包含），null 表示无上限")
    private Double maxYears;

    @FieldDescribe("发放额度")
    private Double amount;

    public boolean match(double years) {
        boolean lowerOk = (minYears == null) || (years >= minYears);
        boolean upperOk = (maxYears == null) || (years < maxYears);
        return lowerOk && upperOk;
    }

    public Double getMinYears() {
        return minYears;
    }

    public void setMinYears(Double minYears) {
        this.minYears = minYears;
    }

    public Double getMaxYears() {
        return maxYears;
    }

    public void setMaxYears(Double maxYears) {
        this.maxYears = maxYears;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

     
    

}