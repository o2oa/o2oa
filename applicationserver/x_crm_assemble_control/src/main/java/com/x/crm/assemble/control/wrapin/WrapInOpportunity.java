package com.x.crm.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.crm.core.entity.Opportunity;

public class WrapInOpportunity extends Opportunity {
	private static final long serialVersionUID = -8051251287263083564L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);
}
