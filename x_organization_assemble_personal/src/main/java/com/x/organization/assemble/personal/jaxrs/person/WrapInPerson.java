package com.x.organization.assemble.personal.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Person;

@Wrap(Person.class)
public class WrapInPerson extends Person {

	private static final long serialVersionUID = -5459590722817246219L;

	public static List<String> Excludes = new ArrayList<>();

	static {
		Excludes.add(JpaObject.ID);
		Excludes.add(JpaObject.DISTRIBUTEFACTOR);
		Excludes.add("password");
	}

}
