package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "消息通讯", packageName = "com.x.message.assemble.communicate", containerEntities = {
		"com.x.message.core.entity.Instant", "com.x.message.core.entity.Message",
		"com.x.message.core.entity.Mass" }, storeJars = { "x_message_core_entity", "x_meeting_core_entity",
				"x_processplatform_core_entity", "x_organization_core_express" })
public class x_message_assemble_communicate extends Deployable {
}
