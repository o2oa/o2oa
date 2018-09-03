package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;

public class x_message_assemble_communicate extends AssembleA {

	public static final String name = "消息通讯";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.message.core.entity.Message");
		dependents.add(x_base_core_project.class);
		dependents.add(x_message_core_entity.class);
		dependents.add(x_meeting_core_entity.class);
		dependents.add(x_processplatform_core_entity.class);
		dependents.add(x_organization_core_express.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
