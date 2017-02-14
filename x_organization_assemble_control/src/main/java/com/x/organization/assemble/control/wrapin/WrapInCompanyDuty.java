package com.x.organization.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.CompanyDuty;

@Wrap(CompanyDuty.class)
public class WrapInCompanyDuty extends CompanyDuty {

	private static final long serialVersionUID = -754726178114690770L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
