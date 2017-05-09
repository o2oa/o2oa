package com.x.organization.assemble.control.alpha.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentAttribute;

@Wrap(DepartmentAttribute.class)
public class WrapInDepartmentAttribute extends DepartmentAttribute {

	private static final long serialVersionUID = 2077525386003180677L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);
}
