package com.x.program.init;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.manticore.h2.H2MigrationTool;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.program.init.Missions.Mission;

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
			Path path = Config.path_local_repository_data(true).resolve(H2Tools.FILENAME_DATABASE);
			Path backup = Config.path_local_repository_data(true)
					.resolve(H2Tools.FILENAME_DATABASE + "." + DateTools.now());
			Files.copy(path, backup);
			H2MigrationTool tool = new H2MigrationTool();
			tool.migrateAuto(null, path.toAbsolutePath().toString(), H2Tools.USER, Config.token().getPassword(), null,
					"COMPRESSION ZIP", "VARIABLE_BINARY", true, true);
			H2Tools.localRepositoryDataH2Version(jarVersion.get());
		} catch (Exception e) {
			throw new ExceptionMissionExecute(e);
		}
	}
}
