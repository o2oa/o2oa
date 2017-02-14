package com.x.organization.assemble.express.jaxrs.updatepersonattribute;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.PersonAttribute;

@Wrap(PersonAttribute.class)
public class WrapInUpdatePersonAttribute extends PersonAttribute {

	private static final long serialVersionUID = 5904343866929456837L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}