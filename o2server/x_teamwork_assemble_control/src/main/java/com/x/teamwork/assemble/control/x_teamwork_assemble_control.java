package com.x.teamwork.assemble.control;

import com.x.base.core.project.Deployable;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.CUSTOM, name = "团队管理", packageName = "com.x.teamwork.assemble.control", containerEntities = {
		"com.x.teamwork.core.entity.Attachment","com.x.teamwork.core.entity.BatchOperation","com.x.teamwork.core.entity.Chat","com.x.teamwork.core.entity.ChatContent","com.x.teamwork.core.entity.Config","com.x.teamwork.core.entity.CustomExtFieldRele","com.x.teamwork.core.entity.Dynamic"
		,"com.x.teamwork.core.entity.DynamicDetail","com.x.teamwork.core.entity.Priority","com.x.teamwork.core.entity.Project","com.x.teamwork.core.entity.ProjectConfig","com.x.teamwork.core.entity.ProjectDetail","com.x.teamwork.core.entity.ProjectGroup"
		,"com.x.teamwork.core.entity.ProjectGroupRele","com.x.teamwork.core.entity.ProjectGroupRele","com.x.teamwork.core.entity.ProjectGroupRele","com.x.teamwork.core.entity.ProjectTemplate","com.x.teamwork.core.entity.Review","com.x.teamwork.core.entity.SystemConfig"
		,"com.x.teamwork.core.entity.SystemConfig","com.x.teamwork.core.entity.PersistenceProperties","com.x.teamwork.core.entity.SystemConfigLobValue","com.x.teamwork.core.entity.Task","com.x.teamwork.core.entity.TaskDetail","com.x.teamwork.core.entity.TaskExtField"
		,"com.x.teamwork.core.entity.TaskExtField","com.x.teamwork.core.entity.TaskGroup","com.x.teamwork.core.entity.TaskGroupRele","com.x.teamwork.core.entity.TaskList","com.x.teamwork.core.entity.TaskListRele","com.x.teamwork.core.entity.TaskListTemplate"
		,"com.x.teamwork.core.entity.TaskRelevance","com.x.teamwork.core.entity.TaskStatuType","com.x.teamwork.core.entity.TaskTag","com.x.teamwork.core.entity.TaskTagRele","com.x.teamwork.core.entity.TaskView"}, 
		storeJars = { "x_organization_core_entity",
				"x_organization_core_express" }, customJars = { "x_teamwork_core_entity" })
public class x_teamwork_assemble_control extends Deployable {
}
