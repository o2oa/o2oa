package com.x.organization.assemble.control.alpha.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.PersonAttribute;

@Wrap(PersonAttribute.class)
public class WrapInPersonAttribute extends PersonAttribute {

	private static final long serialVersionUID = 370024636157241213L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
