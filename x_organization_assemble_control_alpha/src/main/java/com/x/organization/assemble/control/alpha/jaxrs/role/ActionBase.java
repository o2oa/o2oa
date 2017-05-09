package com.x.organization.assemble.control.alpha.jaxrs.role;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.control.alpha.wrapin.WrapInRole;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutRole;
import com.x.organization.core.entity.Role;

public class ActionBase {
	protected static BeanCopyTools<Role, WrapOutRole> outCopier = BeanCopyToolsBuilder.create(Role.class,
			WrapOutRole.class, WrapOutRole.Excludes);

	protected static BeanCopyTools<WrapInRole, Role> inCopier = BeanCopyToolsBuilder.create(WrapInRole.class,
			Role.class, WrapInRole.Excludes);
}
