package com.x.program.init;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.ZipTools;
import com.x.program.init.Missions.Mission;

public class MissionRestore implements Mission {

	private String stamp;

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	@Override
	public void execute() {
		try {
			Path path = Config.path_local_temp(true).resolve(getStamp() + ".zip");
			if (!ZipTools.isZipFile(path)) {
				throw new ExceptionMissionExecute("file is not zip file format.");
			}
			ZipTools.unZip(path.toFile(), null, Config.path_local_dump(true).resolve("dumpData_" + getStamp()).toFile(),
					true, StandardCharsets.UTF_8);
			if (BooleanUtils.isTrue(Config.externalDataSources().enable())) {
				Config.resource_commandQueue().add("start dataSkipInit");
				Thread.sleep(2000);
				Config.resource_commandQueue().add("ctl -initResourceFactory");
				Thread.sleep(2000);
			}
			Config.resource_commandQueue().add("ctl -rd " + getStamp());
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new ExceptionMissionExecute(ie);
		} catch (Exception e) {
			throw new ExceptionMissionExecute(e);
		}
	}

}