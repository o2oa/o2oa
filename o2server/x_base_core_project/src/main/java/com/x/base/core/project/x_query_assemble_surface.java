package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "数据查询", packageName = "com.x.query.assemble.surface", containerEntities = {
        "com.x.query.core.entity.Item", "com.x.query.core.entity.Query",
        "com.x.query.core.entity.View", "com.x.query.core.entity.Stat",
//        "com.x.query.core.entity.Reveal",
//        "com.x.query.core.entity.segment.Word", "com.x.query.core.entity.segment.Entry",
        "com.x.query.core.entity.neural.Entry", "com.x.query.core.entity.neural.InText",
        "com.x.query.core.entity.neural.OutText", "com.x.query.core.entity.neural.InValue",
        "com.x.query.core.entity.neural.OutValue", "com.x.query.core.entity.neural.Model",
        "com.x.query.core.entity.schema.Table", "com.x.query.core.entity.schema.Statement",
        "com.x.query.core.entity.ImportModel", "com.x.query.core.entity.ImportRecord",
        "com.x.query.core.entity.ImportRecordItem", "com.x.processplatform.core.entity.content.Task",
        "com.x.processplatform.core.entity.content.TaskCompleted", "com.x.processplatform.core.entity.content.Read",
        "com.x.processplatform.core.entity.content.ReadCompleted", "com.x.processplatform.core.entity.content.Review",
        "com.x.processplatform.core.entity.content.Work", "com.x.processplatform.core.entity.content.WorkCompleted",
        "com.x.processplatform.core.entity.element.Application", "com.x.processplatform.core.entity.element.Process",
        "com.x.processplatform.core.entity.content.Attachment",
        "com.x.cms.core.entity.Document", "com.x.cms.core.entity.Review", "com.x.cms.core.entity.AppInfo",
        "com.x.cms.core.entity.CategoryInfo", "com.x.cms.core.entity.Document",
        "com.x.cms.core.entity.DocumentViewRecord", "com.x.cms.core.entity.DocumentCommentInfo",
        "com.x.cms.core.entity.element.File", "com.x.cms.core.entity.FileInfo",
        "com.x.cms.core.entity.DocumentCommentContent", "com.x.cms.core.entity.DocumentCommentCommend",
        "com.x.organization.core.entity.Person", "com.x.organization.core.entity.Unit",
        "com.x.organization.core.entity.Group", "com.x.organization.core.entity.Role",
        "com.x.organization.core.entity.Identity", "com.x.general.core.entity.GeneralFile" }, storageTypes = {
                StorageType.processPlatform, StorageType.cms, StorageType.general }, storeJars = {
                        "x_query_core_entity", "x_organization_core_entity", "x_organization_core_express",
                        "x_processplatform_core_entity", "x_cms_core_entity", "x_query_core_express",
                        "x_general_core_entity" })
public class x_query_assemble_surface extends Deployable {
}
