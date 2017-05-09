package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.CompanyDuty;

@Wrap(CompanyDuty.class)
public class WrapOutCompanyDuty extends CompanyDuty {

	private static final long serialVersionUID = 8234461003187026675L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}