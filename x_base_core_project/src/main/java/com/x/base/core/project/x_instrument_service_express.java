package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;

public class x_instrument_service_express extends Service {

	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.instrument.core.entity.Captcha");
		containerEntities.add("com.x.instrument.core.entity.Code");
		containerEntities.add("com.x.instrument.core.entity.Schedule");
		containerEntities.add("com.x.instrument.core.entity.Timer");
		containerEntities.add("com.x.instrument.core.entity.log.PromptErrorLog");
		containerEntities.add("com.x.instrument.core.entity.log.UnexpectedErrorLog");
		containerEntities.add("com.x.instrument.core.entity.log.WarnLog");
		containerEntities.add("com.x.organization.core.entity.Person");
		dependents.add(x_instrument_core_entity.class);
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
