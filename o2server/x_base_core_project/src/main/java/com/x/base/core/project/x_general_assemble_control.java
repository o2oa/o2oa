package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_general_assemble_control extends AssembleA {

	public static final String name = "公共模块";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.general.core.entity.area.District");
		usedStorageTypes.add(StorageType.file);
		dependents.add(x_base_core_project.class);
		dependents.add(x_collaboration_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
		dependents.add(x_general_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
