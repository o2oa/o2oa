package com.x.organization.assemble.control.alpha.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentAttribute;

@Wrap(DepartmentAttribute.class)
public class WrapOutDepartmentAttribute extends DepartmentAttribute {

	private static final long serialVersionUID = 3647567425535685700L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
}
