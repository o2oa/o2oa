package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_mind_assemble_control extends AssembleA {

	public static final String name = "脑图";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.mind.entity.MindBaseInfo");
		containerEntities.add("com.x.mind.entity.MindContentInfo");
		containerEntities.add("com.x.mind.entity.MindFolderInfo");
		containerEntities.add("com.x.mind.entity.MindIconInfo");
		containerEntities.add("com.x.mind.entity.MindRecycleInfo");
		containerEntities.add("com.x.mind.entity.MindShareRecord");
		containerEntities.add("com.x.mind.entity.MindVersionInfo");
		containerEntities.add("com.x.mind.entity.MindVersionContent");
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_mind_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_mind_assemble_control o = new x_mind_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
