package com.x.organization.assemble.express.jaxrs.person;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Person;

class ActionBase extends StandardJaxrsAction {

	protected static BeanCopyTools<Person, WrapOutPerson> outCopier = BeanCopyToolsBuilder.create(Person.class,
			WrapOutPerson.class, null, WrapOutPerson.Excludes);

}
