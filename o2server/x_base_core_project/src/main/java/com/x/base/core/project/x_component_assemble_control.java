package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组件")
public class x_component_assemble_control extends AssembleA {

	public x_component_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.component.core.entity.Component");
		dependency.storeJars.add(x_component_core_entity.class.getSimpleName());
	}
}
