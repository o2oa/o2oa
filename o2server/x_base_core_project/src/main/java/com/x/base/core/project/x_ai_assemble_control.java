package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "AI大模型对接", packageName = "com.x.ai.assemble.control", containerEntities = {
        "com.x.ai.core.entity.File", "com.x.processplatform.core.entity.content.Attachment",
        "com.x.ai.core.entity.AiModel", "com.x.ai.core.entity.Clue", "com.x.ai.core.entity.Completion",
        "com.x.cms.core.entity.FileInfo", "com.x.cms.core.entity.Document","com.x.query.core.entity.Item"},
        storeJars = {"x_ai_core_entity", "x_organization_core_entity", "x_organization_core_express",
                "x_processplatform_core_entity", "x_cms_core_entity", "x_query_core_entity"},
        storageTypes = {StorageType.custom, StorageType.cms, StorageType.processPlatform})
public class x_ai_assemble_control extends Deployable {

}
