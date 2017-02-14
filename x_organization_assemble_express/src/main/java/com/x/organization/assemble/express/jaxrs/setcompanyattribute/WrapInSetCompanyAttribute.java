package com.x.organization.assemble.express.jaxrs.setcompanyattribute;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.CompanyAttribute;

@Wrap(CompanyAttribute.class)
public class WrapInSetCompanyAttribute extends CompanyAttribute {

	private static final long serialVersionUID = -5620185205465842520L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}