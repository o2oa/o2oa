package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.DepartmentDuty;

@Wrap(DepartmentDuty.class)
public class WrapOutDepartmentDuty extends DepartmentDuty {

	private static final long serialVersionUID = -1040484726860333839L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

}