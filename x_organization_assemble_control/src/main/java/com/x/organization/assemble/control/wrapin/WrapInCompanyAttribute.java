package com.x.organization.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.CompanyAttribute;

@Wrap(CompanyAttribute.class)
public class WrapInCompanyAttribute extends CompanyAttribute {

	private static final long serialVersionUID = -7527954993386512109L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);
	
}