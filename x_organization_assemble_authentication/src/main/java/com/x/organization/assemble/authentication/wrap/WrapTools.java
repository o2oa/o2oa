package com.x.organization.assemble.authentication.wrap;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;
import com.x.organization.core.entity.Person;

public class WrapTools {
	public static BeanCopyTools<Person, WrapOutAuthentication> authenticationOutCopier = BeanCopyToolsBuilder
			.create(Person.class, WrapOutAuthentication.class, null, WrapOutAuthentication.Excludes);
}
