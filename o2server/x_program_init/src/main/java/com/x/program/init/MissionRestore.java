package com.x.program.init;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Optional;
import java.util.stream.Stream;

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
	public void execute(Missions.Messages messages) {
		messages.head(MissionRestore.class.getSimpleName());
		try {
			messages.msg("executing");
			Path path = Config.path_local_temp(true).resolve(getStamp() + ".zip");
			if (!ZipTools.isZipFile(path)) {
				throw new ExceptionMissionExecute("file is not zip file format.");
			}
			Path unzipFolder = Config.path_local_temp(true).resolve(getStamp());
			ZipTools.unZip(path.toFile(), null, unzipFolder.toFile(), true, StandardCharsets.UTF_8);
			if ((null == Config.externalDataSources().enable())
					|| BooleanUtils.isNotTrue(Config.externalDataSources().enable())) {
				Config.resource_commandQueue().add("start dataSkipInit");
				Config.resource_commandQueue().add("ctl -initResourceFactory");
				// 命令队列是用多线程运行的,后续如果有ctl -initResourceFactory对目录有操作,可能导致重复删除目录冲突.
				Thread.sleep(5000);
			}
			if ((null == Config.externalStorageSources())
					|| BooleanUtils.isNotTrue(Config.externalStorageSources().getEnable())) {
				Config.resource_commandQueue().add("start storageSkipInit");
				// 命令队列是用多线程运行的,后续如果有ctl -initResourceFactory对目录有操作,可能导致重复删除目录冲突.
				Thread.sleep(5000);
			}
			Optional<Path> folder = locationFolder(unzipFolder);
			if (folder.isPresent()) {
				Config.resource_commandQueue().add("ctl -rd " + folder.get().toAbsolutePath().toString());
				Config.resource_commandTerminatedSignal_ctl_rd().take();// 等待执行完成信号.
				messages.msg("success");
			} else {
				messages.err("can not find catalog.json in folder:{}.", unzipFolder);
			}
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			messages.err(ie.getMessage());
			throw new ExceptionMissionExecute(ie);
		} catch (Exception e) {
			messages.err(e.getMessage());
			throw new ExceptionMissionExecute(e);
		}
	}

	private static Optional<Path> locationFolder(Path path) throws IOException {
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/catalog.json");
		try (Stream<Path> stream = Files.walk(path, 10)) {
			Optional<Path> opt = stream.filter(pathMatcher::matches).findFirst();
			if (opt.isPresent()) {
				return Optional.of(opt.get().getParent().toAbsolutePath());
			}
		}
		return Optional.empty();
	}

}