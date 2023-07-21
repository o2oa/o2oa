package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "会议管理", packageName = "com.x.meeting.assemble.control", containerEntities = {
		"com.x.meeting.core.entity.Building", "com.x.meeting.core.entity.Room", "com.x.meeting.core.entity.Meeting",
		"com.x.meeting.core.entity.Attachment", "com.x.meeting.core.entity.MeetingConfig" }, storageTypes = { StorageType.meeting }, storeJars = {
				"x_organization_core_entity", "x_organization_core_express", "x_meeting_core_entity" })
public class x_meeting_assemble_control extends Deployable {
}
