package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;

public class x_query_service_processing extends ServiceA {

	public static final String name = "数据处理";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.query.core.entity.Item");
		containerEntities.add("com.x.query.core.entity.Query");
		containerEntities.add("com.x.query.core.entity.View");
		containerEntities.add("com.x.query.core.entity.Stat");
		containerEntities.add("com.x.query.core.entity.Reveal");
		containerEntities.add("com.x.query.core.entity.segment.Word");
		containerEntities.add("com.x.query.core.entity.segment.Entry");
		containerEntities.add("com.x.query.core.entity.neural.Entry");
		containerEntities.add("com.x.query.core.entity.neural.InText");
		containerEntities.add("com.x.query.core.entity.neural.OutText");
		containerEntities.add("com.x.query.core.entity.neural.InValue");
		containerEntities.add("com.x.query.core.entity.neural.OutValue");
		containerEntities.add("com.x.query.core.entity.neural.Model");
		containerEntities.add("com.x.processplatform.core.entity.content.Review");
		containerEntities.add("com.x.processplatform.core.entity.content.Work");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		containerEntities.add("com.x.cms.core.entity.Document");
		containerEntities.add("com.x.cms.core.entity.FileInfo");
		containerEntities.add("com.x.cms.core.entity.AppInfo");
		containerEntities.add("com.x.cms.core.entity.CategoryInfo");
		usedStorageTypes.add(StorageType.processPlatform);
		usedStorageTypes.add(StorageType.cms);
		dependents.add(x_base_core_project.class);
		dependents.add(x_query_core_entity.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_processplatform_core_entity.class);
		dependents.add(x_cms_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
