package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.FactorDistributionPolicy;
import com.x.base.core.project.annotation.FieldDescribe;

public class Slice extends ConfigObject {

	private static final Boolean DEFAULT_ENABLE = false;
	private static final String DEFAULT_DISTRIBUTIONPOLICY = FactorDistributionPolicy.class.getName();

	public Slice() {
		this.enable = DEFAULT_ENABLE;
		this.distributionPolicy = DEFAULT_DISTRIBUTIONPOLICY;
	}

	public static Slice defaultInstance() {
		return new Slice();
	}

	@FieldDescribe("是否启用切片特性")
	private Boolean enable;
	@FieldDescribe("分布策略")
	private String distributionPolicy;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getDistributionPolicy() {
		return StringUtils.isEmpty(this.distributionPolicy) ? DEFAULT_DISTRIBUTIONPOLICY : this.distributionPolicy;
	}

	public void setDistributionPolicy(String distributionPolicy) {
		this.distributionPolicy = distributionPolicy;
	}

}
