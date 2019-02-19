package com.x.strategydeploy.assemble.control;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.AssembleA;
import com.x.base.core.project.x_collaboration_core_message;
import com.x.base.core.project.x_organization_core_entity;
import com.x.base.core.project.x_organization_core_express;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.CUSTOM, name = "战略工作")
public class x_strategydeploy_assemble_control extends AssembleA {

	public x_strategydeploy_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.strategydeploy.core.entity.Attachment");
		dependency.containerEntities.add("com.x.strategydeploy.core.entity.StrategyDeploy");
		dependency.containerEntities.add("com.x.strategydeploy.core.entity.MeasuresInfo");
		dependency.containerEntities.add("com.x.strategydeploy.core.entity.KeyworkInfo");
		dependency.containerEntities.add("com.x.strategydeploy.core.entity.StrategyConfigSys");
		dependency.storageTypes.add(StorageType.strategyDeploy.toString());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
		dependency.customJars.add("x_strategydeploy_core_entity");
	}
}
