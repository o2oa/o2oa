package com.x.organization.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Identity;

@Wrap(Identity.class)
public class WrapInIdentity extends Identity {

	private static final long serialVersionUID = 899945360828753292L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
