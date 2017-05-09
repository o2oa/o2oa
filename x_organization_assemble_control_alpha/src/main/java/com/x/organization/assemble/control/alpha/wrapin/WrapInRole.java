package com.x.organization.assemble.control.alpha.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Role;

@Wrap(Role.class)
public class WrapInRole extends Role {

	private static final long serialVersionUID = -9056030012291431779L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);
}
