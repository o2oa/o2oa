package com.x.program.init;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.ZipTools;
import com.x.program.init.Missions.Mission;

public class MissionRestore implements Mission {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void execute() {
		try {
			Path path = Config.path_local_temp(true).resolve(getName() + ".zip");
			ZipTools.unZip(path.toFile(), null, Config.path_local_dump(true).resolve("dumpData_" + getName()).toFile(),
					true, StandardCharsets.UTF_8);
			Config.resource_commandQueue().add("ctl -rd " + name);
		} catch (Exception e) {
			throw new ExceptionMissionExecute(e);
		}
	}

}