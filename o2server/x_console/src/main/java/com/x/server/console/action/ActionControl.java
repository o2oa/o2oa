package com.x.server.console.action;

import java.util.Objects;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

/*
@author zhourui

*/
public class ActionControl extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionControl.class);

	private static Options options = new Options();

	private static final String CMD_PPE = "ppe";
	private static final String CMD_OS = "os";
	private static final String CMD_TS = "ts";
	private static final String CMD_HD = "hd";
	private static final String CMD_TD = "td";
	private static final String CMD_EC = "ec";
	private static final String CMD_DD = "dd";
	private static final String CMD_DS = "ds";
	private static final String CMD_RD = "rd";
	private static final String CMD_RS = "rs";
	private static final String CMD_CLH2 = "clh2";
	private static final String CMD_UF = "uf";
	private static final String CMD_DDL = "ddl";

	private static final int REPEAT_MAX = 100;
	private static final int REPEAT_MIN = 1;

	public void execute(String... args) {
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options(), args);
			if (cmd.hasOption(CMD_PPE)) {
				ppe(cmd);
			} else if (cmd.hasOption(CMD_OS)) {
				os(cmd);
			} else if (cmd.hasOption(CMD_TS)) {
				ts(cmd);
			} else if (cmd.hasOption(CMD_HD)) {
				hd(cmd);
			} else if (cmd.hasOption(CMD_TD)) {
				td(cmd);
			} else if (cmd.hasOption(CMD_EC)) {
				ec(cmd);
			} else if (cmd.hasOption(CMD_DD)) {
				dd(cmd);
			} else if (cmd.hasOption(CMD_DS)) {
				ds(cmd);
			} else if (cmd.hasOption(CMD_RD)) {
				rd(cmd);
			} else if (cmd.hasOption(CMD_RS)) {
				rs(cmd);
			} else if (cmd.hasOption(CMD_CLH2)) {
				clh2(cmd);
			} else if (cmd.hasOption(CMD_UF)) {
				uf(cmd);
			} else if (cmd.hasOption(CMD_DDL)) {
				ddl(cmd);
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("control command", options);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Options options() {
		options.addOption(ppeOption());
		options.addOption(osOption());
		options.addOption(tsOption());
		options.addOption(hdOption());
		options.addOption(tdOption());
		options.addOption(ecOption());
		options.addOption(ddOption());
		options.addOption(dsOption());
		options.addOption(rdOption());
		options.addOption(rsOption());
		options.addOption(clh2Option());
		options.addOption(ufOption());
		options.addOption(ddlOption());
		return options;
	}

	private static Option ppeOption() {
		return Option.builder(CMD_PPE).longOpt("processPlatformExecutor").hasArg(false).desc("显示流程平台执行线程状态.").build();
	}

	private static Option osOption() {
		return Option.builder("os").longOpt("operatingSystem").argName("repeat").numberOfArgs(1).optionalArg(true)
				.hasArgs().desc("显示操作系统信息,间隔2秒.").build();

	}

	private static Option tsOption() {
		return Option.builder("ts").longOpt("threadStatus").argName("repeat").optionalArg(true).hasArgs()
				.desc("服务器线程状态,间隔2秒.合并多次执行线程信息到最后一份日志.").build();
	}

	private static Option hdOption() {
		return Option.builder("hd").longOpt("heapDump").hasArg(false).desc("生成堆转储文件.").build();
	}

	private static Option tdOption() {
		return Option.builder("td").longOpt("threadDump").argName("count").optionalArg(true).hasArg()
				.desc("导出对比线程状态,间隔5秒.").build();
	}

	private static Option ecOption() {
		return Option.builder("ec").longOpt("eraseContent").argName("type").hasArg().optionalArg(false)
				.desc("清空实例数据,保留设计数据,type可选值 bbs cms log processPlatform.").build();
	}

	private static Option clh2Option() {
		return Option.builder("clh2").longOpt("compactLocalH2").desc("压缩本地H2数据库.").build();
	}

	private static Option ddOption() {
		return Option.builder("dd").longOpt("dumpData").argName("path").hasArg().optionalArg(true)
				.desc("导出数据库服务器的数据转换成json格式保存到本地文件.").build();
	}

	private static Option dsOption() {
		return Option.builder("ds").longOpt("dumpStorage").argName("path").hasArg().optionalArg(true)
				.desc("导出存储服务器的文件数据转换成json格式保存到本地文件.").build();
	}

	private static Option rdOption() {
		return Option.builder("rd").longOpt("restoreData").argName("path or date").hasArg()
				.desc("将导出的json格式数据恢复到数据库服务器.").build();
	}

	private static Option rsOption() {
		return Option.builder("rs").longOpt("restoreStorage").argName("path or date").hasArg()
				.desc("将导出的json格式文件数据恢复到存储服务器.").build();
	}

	private static Option ufOption() {
		return Option.builder("uf").longOpt("updateFile").argName("path").hasArg()
				.desc("升级服务器,升级前请注意备份.").build();
	}

	private static Option ddlOption() {
		return Option.builder("ddl").longOpt("DataDefinitionLanguage").argName("type").hasArg()
				.desc("导出数据定义语句:建表语句:build,数据库创建:createDB,数据库删除dropDB.").build();
	}

	private void ec(CommandLine cmd) throws Exception {
		if (BooleanUtils.isNotTrue(Config.currentNode().getEraseContentEnable())) {
			logger.print("erase content is disabled.");
		}
		String type = Objects.toString(cmd.getOptionValue("ec"));
		switch (type) {
			case "processPlatform":
				new EraseContentProcessPlatform().execute();
				break;
			case "bbs":
				new EraseContentBbs().execute();
				break;
			case "cms":
				new EraseContentCms().execute();
				break;
			case "log":
				new EraseContentLog().execute();
				break;
			default:
				logger.print("type may be processPlatform bbs cms log.");
		}
	}

	private void td(CommandLine cmd) throws Exception {
		Integer count = this.getArgInteger(cmd, CMD_TD, 10);
		ThreadDump threadDump = new ThreadDump();
		threadDump.execute(count);
	}

	private void clh2(CommandLine cmd) throws Exception {
		CompactLocalH2 compactLocalH2 = new CompactLocalH2();
		compactLocalH2.execute();
	}

	private void dd(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_DD), "");
		DumpData dumpData = new DumpData();
		dumpData.execute(path);
	}

	private void ds(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_DS), "");
		DumpStorage dumpStorage = new DumpStorage();
		dumpStorage.execute(path);
	}

	private void rd(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_RD), "");
		RestoreData restoreData = new RestoreData();
		restoreData.execute(path);
	}

	private void rs(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_RS), "");
		RestoreStorage restoreStorage = new RestoreStorage();
		restoreStorage.execute(path);
	}

	private void ts(CommandLine cmd) {
		final Integer repeat = this.getArgInteger(cmd, CMD_TS, 1);
		ThreadStatus threadStatus = new ThreadStatus(repeat);
		threadStatus.start();
	}

	private void ppe(CommandLine cmd) throws Exception {
		ProcessPlatformExecutor processPlatformExecutor = new ProcessPlatformExecutor();
		processPlatformExecutor.execute();
	}

	private void os(CommandLine cmd) {
		final Integer command = this.getArgInteger(cmd, "os", 1);
		OperatingSystem operatingSystem = new OperatingSystem(command);
		operatingSystem.start();
	}

	private void hd(CommandLine cmd) throws Exception {
		HeapDump heapDump = new HeapDump();
		heapDump.execute();
	}

	private void uf(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_UF), "");
		UpdateFile updateFile= new UpdateFile();
		updateFile.execute(path);
	}

	private void ddl(CommandLine cmd) throws Exception {
		String type = Objects.toString(cmd.getOptionValue(CMD_DDL), "");
		Ddl ddl= new Ddl();
		ddl.execute(type);
	}

	private Integer getArgInteger(CommandLine cmd, String opt, Integer defaultValue) {
		Integer repeat = defaultValue;
		String r = cmd.getOptionValue(opt);
		if (NumberUtils.isParsable(r)) {
			repeat = NumberUtils.toInt(r);
		}
		if (repeat < REPEAT_MIN || repeat > REPEAT_MAX) {
			repeat = REPEAT_MIN;
		}
		return repeat;
	}

}