package com.x.organization.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.PersonAttribute;

@Wrap(PersonAttribute.class)
public class WrapOutPersonAttribute extends PersonAttribute {

	private static final long serialVersionUID = 4155633357138055904L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);
	private Long rank;

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

}
