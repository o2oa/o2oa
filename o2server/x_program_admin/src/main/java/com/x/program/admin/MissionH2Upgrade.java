package com.x.program.admin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.manticore.h2.H2MigrationTool;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.admin.Missions.Mission;

public class MissionH2Upgrade implements Mission {

	private String fromVersion;

	private String targetVerion;

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public String getTargetVerion() {
		return targetVerion;
	}

	public void setTargetVerion(String targetVerion) {
		this.targetVerion = targetVerion;
	}

	@Override
	public void execute() {
		try {
			Optional<String> jarVersion = H2Tools.jarVersion();
			if (jarVersion.isEmpty()) {
				throw new ExceptionMissionExecute("can not get h2 jar version.");
			}
			Optional<String> localRepositoryDataH2Version = H2Tools.localRepositoryDataH2Version();
			if (localRepositoryDataH2Version.isEmpty()) {
				throw new ExceptionMissionExecute("can not get h2 localRepositoryDataH2Version version.");
			}
			Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
			Path backup = Config.path_local_repository_data(true)
					.resolve(H2Tools.FILENAME_DATABASE + "." + DateTools.now());
			Files.copy(path, backup);
			String[] args = new String[8];
			args[0] = "-u" + H2Tools.USER;
			args[1] = "-p" + Config.token().getPassword();
			args[2] = "-d" + path.toAbsolutePath().toString();
			args[3] = "-oVARIABLE_BINARY";
			args[4] = "-f" + localRepositoryDataH2Version.get();
			args[5] = "-t" + jarVersion.get();
			args[6] = "--force";
			args[7] = "-s"
					+ Config.path_local_repository_data(true).resolve(H2Tools.DATABASE).toAbsolutePath().toString()
					+ ".sql";
//			args[8] = "-c ZIP";
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(XGsonBuilder.toJson(args));
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			H2MigrationTool.main(args);
//			tool.migrateAuto(null, path.toAbsolutePath().toString(), H2Tools.USER, Config.token().getPassword(), null,
//					"COMPRESSION ZIP", "VARIABLE_BINARY", true, true);
			H2Tools.localRepositoryDataH2Version(jarVersion.get());
		} catch (Exception e) {
			throw new ExceptionMissionExecute(e);
		}
	}
}
