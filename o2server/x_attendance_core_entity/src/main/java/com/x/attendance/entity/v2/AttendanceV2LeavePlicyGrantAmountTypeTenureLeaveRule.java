package com.x.attendance.entity.v2;




import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2LeavePlicyGrantAmountTypeTenureLeaveRule extends JsonProperties {
 
    private static final long serialVersionUID = -7000765123966414738L;

    public AttendanceV2LeavePlicyGrantAmountTypeTenureLeaveRule(Double minYears, Double maxYears, Double grantAmount) {
        if (minYears == null && maxYears == null) {
            throw new IllegalArgumentException("minYears 和 maxYears 不能同时为 null");
        }
        if (minYears != null && maxYears != null && minYears >= maxYears) {
            throw new IllegalArgumentException("minYears 必须小于 maxYears");
        }
        if (grantAmount == null || grantAmount < 0) {
            throw new IllegalArgumentException("grantAmount 必须为非负数");
        }
        this.minYears = minYears;
        this.maxYears = maxYears;
        this.grantAmount = grantAmount;
    }

    @FieldDescribe("最小司龄（包含）")
    private Double minYears;

    @FieldDescribe("最大司龄（不包含），null 表示无上限")
    private Double maxYears;

    @FieldDescribe("发放额度")
    private Double grantAmount;

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

    public Double getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Double grantAmount) {
        this.grantAmount = grantAmount;
    }

     
    

}