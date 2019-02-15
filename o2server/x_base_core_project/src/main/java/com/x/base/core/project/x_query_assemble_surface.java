package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "数据查询")
public class x_query_assemble_surface extends AssembleA {

	public x_query_assemble_surface() {
		super();
		dependency.containerEntities.add("com.x.query.core.entity.Item");
		dependency.containerEntities.add("com.x.query.core.entity.Query");
		dependency.containerEntities.add("com.x.query.core.entity.View");
		dependency.containerEntities.add("com.x.query.core.entity.Stat");
		dependency.containerEntities.add("com.x.query.core.entity.Reveal");
		dependency.containerEntities.add("com.x.query.core.entity.segment.Word");
		dependency.containerEntities.add("com.x.query.core.entity.segment.Entry");
		dependency.containerEntities.add("com.x.query.core.entity.neural.Entry");
		dependency.containerEntities.add("com.x.query.core.entity.neural.InText");
		dependency.containerEntities.add("com.x.query.core.entity.neural.OutText");
		dependency.containerEntities.add("com.x.query.core.entity.neural.InValue");
		dependency.containerEntities.add("com.x.query.core.entity.neural.OutValue");
		dependency.containerEntities.add("com.x.query.core.entity.neural.Model");
		dependency.containerEntities.add("com.x.query.core.entity.schema.*");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Review");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Work");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		dependency.containerEntities.add("com.x.cms.core.entity.Document");
		dependency.containerEntities.add("com.x.cms.core.entity.AppInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.CategoryInfo");
		dependency.containerEntities.add("com.x.query.dynamic.entity.*");
		dependency.storageTypes.add(StorageType.processPlatform.toString());
		dependency.storageTypes.add(StorageType.cms.toString());
		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_query_core_express.class.getSimpleName());
	}

//	public static final String name = "数据查询";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.query.core.entity.Item");
//		containerEntities.add("com.x.query.core.entity.Query");
//		containerEntities.add("com.x.query.core.entity.View");
//		containerEntities.add("com.x.query.core.entity.Stat");
//		containerEntities.add("com.x.query.core.entity.Reveal");
//		containerEntities.add("com.x.query.core.entity.segment.Word");
//		containerEntities.add("com.x.query.core.entity.segment.Entry");
//		containerEntities.add("com.x.query.core.entity.neural.Entry");
//		containerEntities.add("com.x.query.core.entity.neural.InText");
//		containerEntities.add("com.x.query.core.entity.neural.OutText");
//		containerEntities.add("com.x.query.core.entity.neural.InValue");
//		containerEntities.add("com.x.query.core.entity.neural.OutValue");
//		containerEntities.add("com.x.query.core.entity.neural.Model");
//		containerEntities.add("com.x.processplatform.core.entity.content.Review");
//		containerEntities.add("com.x.processplatform.core.entity.content.Work");
//		containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
//		containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
//		containerEntities.add("com.x.cms.core.entity.Document");
//		containerEntities.add("com.x.cms.core.entity.AppInfo");
//		containerEntities.add("com.x.cms.core.entity.CategoryInfo");
//		usedStorageTypes.add(StorageType.processPlatform);
//		usedStorageTypes.add(StorageType.cms);
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_query_core_entity.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_processplatform_core_entity.class);
//		dependents.add(x_cms_core_entity.class);
//		dependents.add(x_query_core_express.class);
//	}

}
