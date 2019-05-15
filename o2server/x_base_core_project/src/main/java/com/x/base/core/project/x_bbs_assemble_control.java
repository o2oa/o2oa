package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "论坛")
public class x_bbs_assemble_control extends AssembleA {

	public x_bbs_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.bbs.entity.BBSForumInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSSectionInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSSubjectContent");
		dependency.containerEntities.add("com.x.bbs.entity.BBSReplyInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSSubjectAttachment");
		dependency.containerEntities.add("com.x.bbs.entity.BBSOperationRecord");
		dependency.containerEntities.add("com.x.bbs.entity.BBSUserInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSUserRole");
		dependency.containerEntities.add("com.x.bbs.entity.BBSRoleInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSPermissionRole");
		dependency.containerEntities.add("com.x.bbs.entity.BBSPermissionInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSConfigSetting");
		dependency.containerEntities.add("com.x.bbs.entity.BBSVoteRecord");
		dependency.containerEntities.add("com.x.bbs.entity.BBSVoteOption");
		dependency.containerEntities.add("com.x.bbs.entity.BBSVoteOptionGroup");
		dependency.containerEntities.add("com.x.bbs.entity.BBSSubjectVoteResult");
		dependency.storageTypes.add(StorageType.bbs.toString());
		dependency.storeJars.add(x_bbs_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		// dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
	}
}
