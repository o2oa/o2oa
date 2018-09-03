package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_report_assemble_control extends AssembleA {

	public static final String name = "汇报管理";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanNext");
		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanNextDetail");
		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlan");
		containerEntities.add("com.x.report.core.entity.Report_C_WorkPlanDetail");
		containerEntities.add("com.x.report.core.entity.Report_C_WorkProg");
		containerEntities.add("com.x.report.core.entity.Report_C_WorkProgDetail");
		containerEntities.add("com.x.report.core.entity.Report_I_Base");
		containerEntities.add("com.x.report.core.entity.Report_I_Detail");
        containerEntities.add("com.x.report.core.entity.Report_I_WorkInfo");
		containerEntities.add("com.x.report.core.entity.Report_I_WorkInfoDetail");
		containerEntities.add("com.x.report.core.entity.Report_I_WorkTag");
		containerEntities.add("com.x.report.core.entity.Report_I_WorkTagUnit");
		containerEntities.add("com.x.report.core.entity.Report_P_Permission");
        containerEntities.add("com.x.report.core.entity.Report_P_MeasureInfo");
		containerEntities.add("com.x.report.core.entity.Report_P_Profile");
		containerEntities.add("com.x.report.core.entity.Report_P_ProfileDetail");
		containerEntities.add("com.x.report.core.entity.Report_R_CreateTime");
		containerEntities.add("com.x.report.core.entity.Report_R_View");
		containerEntities.add("com.x.report.core.entity.Report_S_Setting");
		containerEntities.add("com.x.report.core.entity.Report_S_SettingLobValue");
		containerEntities.add("com.x.report.core.entity.Report_I_Ext_Content");
		containerEntities.add("com.x.report.core.entity.Report_I_Ext_ContentDetail");
		
		usedStorageTypes.add( StorageType.report );
		
		dependents.add(x_base_core_project.class);
		dependents.add(x_report_core_entity.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
		// File xLibDir = new File(xLib);
		// File libDir = new File(lib, "WEB-INF/lib");
		// for (Class<? extends Compilable> clz : dependents) {
		// FileUtils.copyDirectory(xLibDir, libDir, new
		// NameFileFilter(clz.getSimpleName() + "-" + VERSION + ".jar"));
		// }
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_report_assemble_control o = new x_report_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
