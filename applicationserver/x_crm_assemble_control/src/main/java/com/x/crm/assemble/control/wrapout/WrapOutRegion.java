package com.x.crm.assemble.control.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.crm.core.entity.CrmRegion;

public class WrapOutRegion extends CrmRegion {

	private static final long serialVersionUID = -5523699412815337150L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}
