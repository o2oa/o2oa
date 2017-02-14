package com.x.organization.assemble.express.jaxrs.setdepartmentattribute;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentAttribute;

@Wrap(DepartmentAttribute.class)
public class WrapInSetDepartmentAttribute extends DepartmentAttribute {

	private static final long serialVersionUID = 6168870242033365536L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}