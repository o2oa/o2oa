package com.x.organization.assemble.control.alpha.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Company;

@Wrap(Company.class)
public class WrapInCompany extends Company {

	private static final long serialVersionUID = -3714280771189234210L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
