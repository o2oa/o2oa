package com.x.base.core.entity;

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.x.base.core.entity.annotation.EntityFieldDescribe;

@MappedSuperclass
public abstract class SliceJpaObject extends JpaObject {

	private static final long serialVersionUID = 805690971791595604L;

	@EntityFieldDescribe("分布式存储标识位.")
	@Column(name = "xdistributeFactor")
	protected Integer distributeFactor = (new Random()).nextInt(1000);

	public Integer getDistributeFactor() {
		return distributeFactor;
	}

	public void setDistributeFactor(Integer distributeFactor) {
		this.distributeFactor = distributeFactor;
	}

}