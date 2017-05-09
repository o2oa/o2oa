package com.x.organization.assemble.control.alpha.jaxrs.personattribute;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.control.alpha.wrapin.WrapInPersonAttribute;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutPersonAttribute;
import com.x.organization.core.entity.PersonAttribute;

public class ActionBase {
	protected static BeanCopyTools<WrapInPersonAttribute, PersonAttribute> inCopier = BeanCopyToolsBuilder
			.create(WrapInPersonAttribute.class, PersonAttribute.class, WrapInPersonAttribute.Excludes);

	protected static BeanCopyTools<PersonAttribute, WrapOutPersonAttribute> outCopier = BeanCopyToolsBuilder
			.create(PersonAttribute.class, WrapOutPersonAttribute.class, WrapOutPersonAttribute.Excludes);
}
