package com.x.organization.assemble.express.jaxrs.wrapout;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.organization.core.entity.Person;

@Wrap(Person.class)
public class WrapOutPerson extends Person {

	private static final long serialVersionUID = -6279010554116422125L;

	public static List<String> Excludes = new ArrayList<>();

	static {
		Excludes.add(JpaObject.DISTRIBUTEFACTOR);
		Excludes.add("sequence");
		Excludes.add("password");
	}
}