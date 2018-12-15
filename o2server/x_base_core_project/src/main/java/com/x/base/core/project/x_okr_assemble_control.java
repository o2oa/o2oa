package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_okr_assemble_control extends AssembleA {

	public static final String name = "OKR";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.okr.entity.OkrAttachmentFileInfo");
		containerEntities.add("com.x.okr.entity.OkrCenterWorkInfo");
		containerEntities.add("com.x.okr.entity.OkrConfigSecretary");
		containerEntities.add("com.x.okr.entity.OkrConfigSystem");
		containerEntities.add("com.x.okr.entity.OkrConfigWorkLevel");
		containerEntities.add("com.x.okr.entity.OkrConfigWorkType");
		containerEntities.add("com.x.okr.entity.OkrTask");
		containerEntities.add("com.x.okr.entity.OkrTaskHandled");
		containerEntities.add("com.x.okr.entity.OkrWorkAuthorizeRecord");
		containerEntities.add("com.x.okr.entity.OkrWorkBaseInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkDetailInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkDynamics");
		containerEntities.add("com.x.okr.entity.OkrWorkPerson");
		containerEntities.add("com.x.okr.entity.OkrWorkReportBaseInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkReportDetailInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkReportPersonLink");
		containerEntities.add("com.x.okr.entity.OkrWorkReportProcessLog");		
		containerEntities.add("com.x.okr.entity.OkrWorkChat");
		containerEntities.add("com.x.okr.entity.OkrStatisticReportContent");
		containerEntities.add("com.x.okr.entity.OkrStatisticReportStatus");
		containerEntities.add("com.x.okr.entity.OkrUserInfo");
		containerEntities.add("com.x.okr.entity.OkrErrorSystemIdentityInfo");
		containerEntities.add("com.x.okr.entity.OkrErrorIdentityRecords");
		containerEntities.add("com.x.okr.entity.OkrWorkAppraiseInfo");
		usedStorageTypes.add(StorageType.okr);
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_okr_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
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
			x_okr_assemble_control o = new x_okr_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
