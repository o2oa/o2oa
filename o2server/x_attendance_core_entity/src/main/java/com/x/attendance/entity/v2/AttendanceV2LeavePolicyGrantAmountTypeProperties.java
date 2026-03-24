package com.x.attendance.entity.v2;

import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2LeavePolicyGrantAmountTypeProperties extends JsonProperties  {
    
    private static final long serialVersionUID = -4872177863446165403L;

    @FieldDescribe("发放额度类型，按年发放才需要, FIXED: 固定额度， SERVICELEN: 按司龄发放")
    private String type;

    @FieldDescribe("发放额度")
    private Double grantAmount = 0.0; 

     @FieldDescribe("按司龄发放规则列表")
    private List<AttendanceV2LeavePlicyGrantAmountTypeTenureLeaveRule> tenureLeaveRules;

    // 根据司龄计算发放额度
    public Double calculateGrantAmount(double yearsOfService) {
        if ("FIXED".equalsIgnoreCase(type)) {
            return grantAmount;
        } else if ("SERVICELEN".equalsIgnoreCase(type)) {
            if (tenureLeaveRules != null) {
                for (AttendanceV2LeavePlicyGrantAmountTypeTenureLeaveRule rule : tenureLeaveRules) {
                    if (rule.match(yearsOfService)) {
                        return rule.getGrantAmount();
                    }
                }
            }
        }
        return 0.0; // 默认返回0
    }

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public Double getGrantAmount() {
         return grantAmount;
     }

     public void setGrantAmount(Double grantAmount) {
         this.grantAmount = grantAmount;
     }

     public List<AttendanceV2LeavePlicyGrantAmountTypeTenureLeaveRule> getTenureLeaveRules() {
         return tenureLeaveRules;
     }

     public void setTenureLeaveRules(List<AttendanceV2LeavePlicyGrantAmountTypeTenureLeaveRule> tenureLeaveRules) {
         this.tenureLeaveRules = tenureLeaveRules;
     }
    

    
}
