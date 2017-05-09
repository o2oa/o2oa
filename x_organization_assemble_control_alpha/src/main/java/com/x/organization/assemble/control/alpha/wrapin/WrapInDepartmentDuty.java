package com.x.organization.assemble.control.alpha.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentDuty;

@Wrap(DepartmentDuty.class)
public class WrapInDepartmentDuty extends DepartmentDuty {

	private static final long serialVersionUID = 1598940938709132497L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
