package com.x.organization.assemble.control.alpha.jaxrs.group;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.organization.assemble.control.alpha.wrapin.WrapInGroup;
import com.x.organization.assemble.control.alpha.wrapout.WrapOutGroup;
import com.x.organization.core.entity.Group;

public class ActionBase {
	protected static BeanCopyTools<Group, WrapOutGroup> outCopier = BeanCopyToolsBuilder.create(Group.class,
			WrapOutGroup.class, null, WrapOutGroup.Excludes);

	protected static BeanCopyTools<WrapInGroup, Group> inCopier = BeanCopyToolsBuilder.create(WrapInGroup.class,
			Group.class, null, WrapInGroup.Excludes);
}
