package com.x.organization.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Group;

@Wrap(Group.class)
public class WrapInGroup extends Group {

	private static final long serialVersionUID = 9081604316339518904L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

}
