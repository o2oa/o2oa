package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_okr_assemble_control extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();

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
		containerEntities.add("com.x.okr.entity.OkrWorkProblemInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkProblemPersonLink");
		containerEntities.add("com.x.okr.entity.OkrWorkProblemProcessLog");
		containerEntities.add("com.x.okr.entity.OkrWorkProcessLink");
		containerEntities.add("com.x.okr.entity.OkrWorkReportBaseInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkReportDetailInfo");
		containerEntities.add("com.x.okr.entity.OkrWorkReportPersonLink");
		containerEntities.add("com.x.okr.entity.OkrWorkReportProcessLog");
		containerEntities.add("com.x.okr.entity.OkrPermissionInfo");
		containerEntities.add("com.x.okr.entity.OkrPersonPermission");
		containerEntities.add("com.x.okr.entity.OkrRoleInfo");
		containerEntities.add("com.x.okr.entity.OkrRolePermission");
		containerEntities.add("com.x.okr.entity.OkrWorkChat");
	}

	protected void custom(File dir, String repositoryPath) throws Exception {
		File repository = new File(repositoryPath);
		File lib = new File(dir, "WEB-INF/lib");
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_express*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_okr_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_collaboration_core_message*.jar"));
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_okr_assemble_control o = new x_okr_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					arg.getCenterCipher(), arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
