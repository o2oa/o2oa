package com.x.organization.assemble.control.alpha.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Department;

@Wrap(Department.class)
public class WrapInDepartment extends Department {

	private static final long serialVersionUID = -2362010201807038576L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);
}
