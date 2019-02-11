package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.gson.XGsonBuilder;

@Module(type = ModuleType.SERVICE, category = ModuleCategory.OFFICIAL, name = "协作服务")
public class x_collaboration_service_message extends ServiceA {

	public x_collaboration_service_message() {
		super();
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
	}

//	public static final Dependency dependency = new Dependency();
//
//	static {
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
//	}
//	public static final String name = "协作服务";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_collaboration_core_message.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_collaboration_core_entity.class);
//	}

}
