package com.x.program.init;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;

import com.google.common.collect.ImmutableMap;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.ExternalDataSource;
import com.x.base.core.project.tools.ZipTools;
import com.x.program.init.Missions.Mission;

public class MissionExternalDataSource implements Mission {

	public static final String TYPE_MYSQL = "mysql";
	public static final String TYPE_POSTGRESQL = "postgresql";
	public static final String TYPE_ORACLE = "oracle";
	public static final String TYPE_SQLSERVER = "sqlserver";
	public static final String TYPE_DB2 = "db2";
	public static final String TYPE_H2 = "h2";
	public static final String TYPE_DM = "dm";
	public static final String TYPE_KINGBASE = "kingbase";
	public static final String TYPE_KINGBASE8 = "kingbase8";
	public static final String TYPE_GBASE = "gbase";
	public static final String TYPE_GBASEMYSQL = "gbasemysql";
	public static final String TYPE_OSCAR = "oscar";
	public static final String TYPE_INFORMIX = "informix";

//	Map<String, ExternalDataSource> immutableMap = ImmutableMap.ofEntries(new AbstractMap.SimpleEntry<>(1, "USA"));

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
			if ((null == Config.externalDataSources().enable())
					|| BooleanUtils.isNotTrue(Config.externalDataSources().enable())) {
				Config.resource_commandQueue().add("start dataSkipInit");
				Thread.sleep(2000);
				Config.resource_commandQueue().add("ctl -initResourceFactory");
				Thread.sleep(2000);
			}
			if ((null == Config.externalStorageSources())
					|| BooleanUtils.isNotTrue(Config.externalStorageSources().getEnable())) {
				Config.resource_commandQueue().add("start storageSkipInit");
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