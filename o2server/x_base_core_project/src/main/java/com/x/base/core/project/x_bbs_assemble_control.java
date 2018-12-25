package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_bbs_assemble_control extends AssembleA {

	public static final String name = "论坛";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.bbs.entity.BBSForumInfo");
		containerEntities.add("com.x.bbs.entity.BBSSectionInfo");
		containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
		containerEntities.add("com.x.bbs.entity.BBSSubjectContent");
		containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
		containerEntities.add("com.x.bbs.entity.BBSReplyInfo");
		containerEntities.add("com.x.bbs.entity.BBSSubjectAttachment");
		containerEntities.add("com.x.bbs.entity.BBSOperationRecord");
		containerEntities.add("com.x.bbs.entity.BBSUserInfo");
		containerEntities.add("com.x.bbs.entity.BBSUserRole");
		containerEntities.add("com.x.bbs.entity.BBSRoleInfo");
		containerEntities.add("com.x.bbs.entity.BBSPermissionRole");
		containerEntities.add("com.x.bbs.entity.BBSPermissionInfo");
		containerEntities.add("com.x.bbs.entity.BBSConfigSetting");

		containerEntities.add("com.x.bbs.entity.BBSVoteRecord");
		containerEntities.add("com.x.bbs.entity.BBSVoteOption");
		containerEntities.add("com.x.bbs.entity.BBSVoteOptionGroup");
		containerEntities.add("com.x.bbs.entity.BBSSubjectVoteResult");
		usedStorageTypes.add(StorageType.bbs);
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_bbs_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_bbs_assemble_control o = new x_bbs_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
