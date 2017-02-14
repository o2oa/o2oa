package com.x.organization.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.organization.core.entity.Person;

public class WrapInPerson extends Person {

	private static final long serialVersionUID = 1571810726944802231L;

	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodifies);

	static {
		Excludes.add("icon");
		Excludes.add("pinyin");
		Excludes.add("pinyinInitial");
		Excludes.add("password");
	}

}
