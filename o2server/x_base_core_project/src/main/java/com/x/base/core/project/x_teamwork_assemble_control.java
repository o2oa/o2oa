package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "工作任务管理", packageName = "com.x.teamwork.assemble.control", 
containerEntities = {
		"com.x.teamwork.core.entity.Project", "com.x.teamwork.core.entity.ProjectDetail",
		"com.x.teamwork.core.entity.ProjectExtFieldRele", "com.x.teamwork.core.entity.ProjectGroup",
		"com.x.teamwork.core.entity.ProjectGroupRele", "com.x.teamwork.core.entity.Task",
		"com.x.teamwork.core.entity.TaskGroup", "com.x.teamwork.core.entity.TaskGroupRele",
		"com.x.teamwork.core.entity.TaskDetail", "com.x.teamwork.core.entity.TaskExtField", "com.x.teamwork.core.entity.TaskList",
		"com.x.teamwork.core.entity.TaskListRele", "com.x.teamwork.core.entity.TaskRelevance",
		"com.x.teamwork.core.entity.TaskView", "com.x.teamwork.core.entity.SystemConfig",
		"com.x.teamwork.core.entity.SystemConfigLobValue", "com.x.teamwork.core.entity.Review",
		"com.x.teamwork.core.entity.BatchOperation", "com.x.teamwork.core.entity.TaskTag", 
		"com.x.teamwork.core.entity.TaskTagRele", "com.x.teamwork.core.entity.Attachment",
		"com.x.teamwork.core.entity.Chat", "com.x.teamwork.core.entity.ChatContent",
		"com.x.teamwork.core.entity.Dynamic", "com.x.teamwork.core.entity.DynamicDetail" }, storeJars = { "x_organization_core_entity",
		"x_organization_core_express", "x_teamwork_core_entity" })
public class x_teamwork_assemble_control extends Deployable {
}
