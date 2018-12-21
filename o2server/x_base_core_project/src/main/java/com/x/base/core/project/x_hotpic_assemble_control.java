package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_hotpic_assemble_control extends AssembleA {

	public static final String name = "热点图片";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.hotpic.entity.HotPictureInfo");
		containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
		containerEntities.add("com.x.cms.core.entity.Document");
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_hotpic_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
		dependents.add(x_cms_core_entity.class);
		dependents.add(x_bbs_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_hotpic_assemble_control o = new x_hotpic_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
