package com.x.base.core.entity.dynamic;


import com.x.base.core.entity.SliceJpaObject;

public class DynamicBaseEntity extends SliceJpaObject {

	private static final long serialVersionUID = -4334572608549855123L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
	}

}
