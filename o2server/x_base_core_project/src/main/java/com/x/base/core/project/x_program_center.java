package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.CENTER, category = ModuleCategory.OFFICIAL, name = "中心", packageName = "com.x.program.center", containerEntities = {
		"com.x.general.core.entity.ApplicationDict", "com.x.general.core.entity.ApplicationDictItem",
		"com.x.general.core.entity.area.District", "com.x.organization.core.entity.Custom",
		"com.x.organization.core.entity.Group", "com.x.organization.core.entity.Identity",
		"com.x.organization.core.entity.Person", "com.x.organization.core.entity.PersonAttribute",
		"com.x.organization.core.entity.Role", "com.x.organization.core.entity.Unit",
		"com.x.organization.core.entity.UnitAttribute", "com.x.organization.core.entity.UnitDuty",
		"com.x.portal.core.entity.Page", "com.x.portal.core.entity.Portal",
		"com.x.processplatform.core.entity.element.Agent", "com.x.processplatform.core.entity.element.Begin",
		"com.x.processplatform.core.entity.element.Cancel", "com.x.processplatform.core.entity.element.Choice",
		"com.x.processplatform.core.entity.element.Delay", "com.x.processplatform.core.entity.element.Embed",
		"com.x.processplatform.core.entity.element.End", "com.x.processplatform.core.entity.element.Invoke",
		"com.x.processplatform.core.entity.element.Manual", "com.x.processplatform.core.entity.element.Merge",
		"com.x.processplatform.core.entity.element.Parallel", "com.x.processplatform.core.entity.element.Publish",
		"com.x.processplatform.core.entity.element.Service", "com.x.processplatform.core.entity.element.Split",
		"com.x.program.center.core.entity.Agent", "com.x.program.center.core.entity.Application",
		"com.x.program.center.core.entity.AppPackApkFile", "com.x.program.center.core.entity.Attachment",
		"com.x.program.center.core.entity.Captcha", "com.x.program.center.core.entity.Code",
		"com.x.program.center.core.entity.DeployLog", "com.x.program.center.core.entity.InstallLog",
		"com.x.program.center.core.entity.Invoke", "com.x.program.center.core.entity.MPWeixinMenu",
		"com.x.program.center.core.entity.PromptErrorLog", "com.x.program.center.core.entity.ScheduleLog",
		"com.x.program.center.core.entity.Script", "com.x.program.center.core.entity.Structure",
		"com.x.program.center.core.entity.UnexpectedErrorLog", "com.x.program.center.core.entity.validation.Bar",
		"com.x.program.center.core.entity.validation.Foo", "com.x.program.center.core.entity.validation.Meta",
		"com.x.program.center.core.entity.WarnLog" }, storageTypes = { StorageType.structure }, storeJars = {
				"x_organization_core_express", "x_program_center_core_entity", "x_attendance_core_entity",
				"x_cms_core_entity", "x_message_core_entity", "x_component_core_entity", "x_file_core_entity",
				"x_meeting_core_entity", "x_okr_core_entity", "x_organization_core_entity",
				"x_processplatform_core_entity", "x_query_core_entity", "x_portal_core_entity",
				"x_general_core_entity" })
public class x_program_center extends Deployable {

}
