package com.x.report.assemble.control;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.AssembleA;
import com.x.base.core.project.x_organization_core_entity;
import com.x.base.core.project.x_organization_core_express;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "汇报管理")
public class x_report_assemble_control extends AssembleA {

	public x_report_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanNext");
		dependency.containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanNextDetail");
		dependency.containerEntities.add("com.x.report.core.entity.Report_C_WorkPlan");
		dependency.containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanDetail");
		dependency.containerEntities.add("com.x.report.core.entity.Report_C_WorkProg");
		dependency.containerEntities.add("com.x.report.core.entity.Report_C_WorkProgDetail");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_Base");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_Detail");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_WorkInfo");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_WorkInfoDetail");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_WorkTag");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_WorkTagUnit");
		dependency.containerEntities.add("com.x.report.core.entity.Report_P_Permission");
		dependency.containerEntities.add("com.x.report.core.entity.Report_P_MeasureInfo");
		dependency.containerEntities.add("com.x.report.core.entity.Report_P_Profile");
		dependency.containerEntities.add("com.x.report.core.entity.Report_P_ProfileDetail");
		dependency.containerEntities.add("com.x.report.core.entity.Report_R_CreateTime");
		dependency.containerEntities.add("com.x.report.core.entity.Report_R_View");
		dependency.containerEntities.add("com.x.report.core.entity.Report_S_Setting");
		dependency.containerEntities.add("com.x.report.core.entity.Report_S_SettingLobValue");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_Ext_Content");
		dependency.containerEntities.add("com.x.report.core.entity.Report_I_Ext_ContentDetail");
		dependency.storageTypes.add(StorageType.report.toString());
		dependency.customJars.add("x_report_core_entity");
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
	}
//	public static final String name = "汇报管理";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

//	static {
//		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanNext");
//		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanNextDetail");
//		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlan");
//		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanDetail");
//		containerEntities.add("com.x.report.core.entity.Report_C_WorkProg");
//		containerEntities.add("com.x.report.core.entity.Report_C_WorkProgDetail");
//		containerEntities.add("com.x.report.core.entity.Report_I_Base");
//		containerEntities.add("com.x.report.core.entity.Report_I_Detail");
//		containerEntities.add("com.x.report.core.entity.Report_I_WorkInfo");
//		containerEntities.add("com.x.report.core.entity.Report_I_WorkInfoDetail");
//		containerEntities.add("com.x.report.core.entity.Report_I_WorkTag");
//		containerEntities.add("com.x.report.core.entity.Report_I_WorkTagUnit");
//		containerEntities.add("com.x.report.core.entity.Report_P_Permission");
//		containerEntities.add("com.x.report.core.entity.Report_P_MeasureInfo");
//		containerEntities.add("com.x.report.core.entity.Report_P_Profile");
//		containerEntities.add("com.x.report.core.entity.Report_P_ProfileDetail");
//		containerEntities.add("com.x.report.core.entity.Report_R_CreateTime");
//		containerEntities.add("com.x.report.core.entity.Report_R_View");
//		containerEntities.add("com.x.report.core.entity.Report_S_Setting");
//		containerEntities.add("com.x.report.core.entity.Report_S_SettingLobValue");
//		containerEntities.add("com.x.report.core.entity.Report_I_Ext_Content");
//		containerEntities.add("com.x.report.core.entity.Report_I_Ext_ContentDetail");
//
//		usedStorageTypes.add(StorageType.report);
//
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_report_core_entity.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//	}

}
