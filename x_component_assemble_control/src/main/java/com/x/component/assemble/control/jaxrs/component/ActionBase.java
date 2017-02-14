package com.x.component.assemble.control.jaxrs.component;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.component.assemble.control.jaxrs.wrapin.WrapInComponent;
import com.x.component.assemble.control.jaxrs.wrapout.WrapOutComponent;
import com.x.component.core.entity.Component;

public class ActionBase {

	protected static BeanCopyTools<Component, WrapOutComponent> outCopier = BeanCopyToolsBuilder.create(Component.class,
			WrapOutComponent.class, null, WrapOutComponent.Excludes);

	protected static BeanCopyTools<WrapInComponent, Component> inCopier = BeanCopyToolsBuilder
			.create(WrapInComponent.class, Component.class, null, WrapInComponent.Excludes);

}
