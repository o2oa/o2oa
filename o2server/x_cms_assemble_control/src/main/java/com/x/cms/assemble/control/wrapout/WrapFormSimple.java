package com.x.cms.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.cms.core.entity.element.wrap.WrapForm;

public class WrapFormSimple extends WrapForm {

	private static final long serialVersionUID = -7495725325510376323L;

	private Long rank;

	public static List<String> excludes = new ArrayList<>(JpaObject.FieldsInvisible);

	static {
		excludes.add("data");
		excludes.add("mobileData");
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

}
