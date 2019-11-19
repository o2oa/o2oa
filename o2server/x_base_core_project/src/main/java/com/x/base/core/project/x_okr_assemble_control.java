package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "OKR", packageName = "com.x.okr.assemble.control", containerEntities = {
		"com.x.okr.entity.OkrAttachmentFileInfo", "com.x.okr.entity.OkrCenterWorkInfo",
		"com.x.okr.entity.OkrConfigSecretary", "com.x.okr.entity.OkrConfigSystem",
		"com.x.okr.entity.OkrConfigWorkLevel", "com.x.okr.entity.OkrConfigWorkType", "com.x.okr.entity.OkrTask",
		"com.x.okr.entity.OkrTaskHandled", "com.x.okr.entity.OkrWorkAuthorizeRecord",
		"com.x.okr.entity.OkrWorkBaseInfo", "com.x.okr.entity.OkrWorkDetailInfo", "com.x.okr.entity.OkrWorkDynamics",
		"com.x.okr.entity.OkrWorkPerson", "com.x.okr.entity.OkrWorkReportBaseInfo",
		"com.x.okr.entity.OkrWorkReportDetailInfo", "com.x.okr.entity.OkrWorkReportPersonLink",
		"com.x.okr.entity.OkrWorkReportProcessLog", "com.x.okr.entity.OkrWorkChat",
		"com.x.okr.entity.OkrStatisticReportContent", "com.x.okr.entity.OkrStatisticReportStatus",
		"com.x.okr.entity.OkrUserInfo", "com.x.okr.entity.OkrErrorSystemIdentityInfo",
		"com.x.okr.entity.OkrErrorIdentityRecords",
		"com.x.okr.entity.OkrWorkAppraiseInfo" }, storageTypes = { StorageType.okr }, storeJars = {
				"x_organization_core_entity", "x_organization_core_express", "x_okr_core_entity" })
public class x_okr_assemble_control extends Deployable {
}
