package com.x.general.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author chengjian
 * @date 2025/02/19 15:41
 **/
public class InvoiceDetail extends JsonProperties {
    @FieldDescribe("项目名称")
    private String name;
    @FieldDescribe("车牌号|规格型号")
    private String model;
    private String unit;
    private Double count;
    private Double price;
    @FieldDescribe("金额")
    private Double amount;
    @FieldDescribe("税率")
    private Double taxRate;
    @FieldDescribe("税额")
    private Double taxAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }
}
