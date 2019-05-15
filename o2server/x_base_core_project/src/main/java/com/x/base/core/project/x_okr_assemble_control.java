package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "OKR")
public class x_okr_assemble_control extends AssembleA {

	public x_okr_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.okr.entity.OkrAttachmentFileInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrCenterWorkInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrConfigSecretary");
		dependency.containerEntities.add("com.x.okr.entity.OkrConfigSystem");
		dependency.containerEntities.add("com.x.okr.entity.OkrConfigWorkLevel");
		dependency.containerEntities.add("com.x.okr.entity.OkrConfigWorkType");
		dependency.containerEntities.add("com.x.okr.entity.OkrTask");
		dependency.containerEntities.add("com.x.okr.entity.OkrTaskHandled");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkAuthorizeRecord");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkBaseInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkDetailInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkDynamics");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkPerson");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkReportBaseInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkReportDetailInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkReportPersonLink");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkReportProcessLog");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkChat");
		dependency.containerEntities.add("com.x.okr.entity.OkrStatisticReportContent");
		dependency.containerEntities.add("com.x.okr.entity.OkrStatisticReportStatus");
		dependency.containerEntities.add("com.x.okr.entity.OkrUserInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrErrorSystemIdentityInfo");
		dependency.containerEntities.add("com.x.okr.entity.OkrErrorIdentityRecords");
		dependency.containerEntities.add("com.x.okr.entity.OkrWorkAppraiseInfo");
		dependency.storageTypes.add(StorageType.okr.toString());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_okr_core_entity.class.getSimpleName());
		// dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
	}
}
