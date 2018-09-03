package com.x.crm.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.crm.core.entity.CrmRegion;

//@Wrap(WrapInCrmRegion.class)
public class WrapInCrmRegion extends CrmRegion {
	private static final long serialVersionUID = -7383949880748554197L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);
}
