package com.x.server.console.action;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.ResourceFactory;

/*
@author zhourui

*/
public class ActionControl extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionControl.class);

	private static final String CMD_OS = "os";
	private static final String CMD_HS = "hs";
	private static final String CMD_HD = "hd";
	private static final String CMD_TD = "td";
	private static final String CMD_EC = "ec";
	private static final String CMD_DD = "dd";
	private static final String CMD_RD = "rd";
	private static final String CMD_CLH2 = "clh2";
	private static final String CMD_UF = "uf";
	private static final String CMD_DDL = "ddl";
	private static final String CMD_RST = "rst";
	private static final String CMD_SC = "sc";
	private static final String CMD_EN = "en";
	private static final String CMD_DE = "de";
	private static final String CMD_GC = "gc";
	private static final String CMD_INITRESOURCEFACTORY = "initResourceFactory";
	private static final String CMD_FLUSHCONFIG = "flushConfig";
	private static final String CMD_REGENERATECONFIG = "regenerateConfig";

	private static final int REPEAT_MAX = 100;
	private static final int REPEAT_MIN = 1;

	public void execute(String... args) {
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options(), args);
			if (cmd.hasOption(CMD_OS)) {
				os(cmd);
			} else if (cmd.hasOption(CMD_HS)) {
				hs(cmd);
			} else if (cmd.hasOption(CMD_HD)) {
				hd(cmd);
			} else if (cmd.hasOption(CMD_TD)) {
				td(cmd);
			} else if (cmd.hasOption(CMD_EC)) {
				ec(cmd);
			} else if (cmd.hasOption(CMD_DD)) {
				dd(cmd);
			} else if (cmd.hasOption(CMD_RD)) {
				rd(cmd);
			} else if (cmd.hasOption(CMD_CLH2)) {
				clh2(cmd);
			} else if (cmd.hasOption(CMD_UF)) {
				uf(cmd);
			} else if (cmd.hasOption(CMD_DDL)) {
				ddl(cmd);
			} else if (cmd.hasOption(CMD_RST)) {
				rst(cmd);
			} else if (cmd.hasOption(CMD_SC)) {
				sc(cmd);
			} else if (cmd.hasOption(CMD_EN)) {
				en(cmd);
			} else if (cmd.hasOption(CMD_DE)) {
				de(cmd);
			} else if (cmd.hasOption(CMD_GC)) {
				gc();
			} else if (cmd.hasOption(CMD_INITRESOURCEFACTORY)) {
				initResourceFactory();
			} else if (cmd.hasOption(CMD_FLUSHCONFIG)) {
				flushConfig();
			} else if (cmd.hasOption(CMD_REGENERATECONFIG)) {
				regenerateConfig();
			} else {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("control command", displayOptions());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Options options() {
		Options options = new Options();
		options.addOption(osOption());
		options.addOption(hsOption());
		options.addOption(hdOption());
		options.addOption(tdOption());
		options.addOption(ecOption());
		options.addOption(ddOption());
		options.addOption(rdOption());
		options.addOption(clh2Option());
		options.addOption(ufOption());
		options.addOption(ddlOption());
		options.addOption(rstOption());
		options.addOption(scOption());
		options.addOption(enOption());
		options.addOption(deOption());
		options.addOption(gcOption());
		options.addOption(initResourceFactoryOption());
		options.addOption(flushConfigOption());
		options.addOption(regenerateConfigOption());
		return options;
	}

	private static Options displayOptions() {
		Options displayOptions = new Options();
		displayOptions.addOption(osOption());
		displayOptions.addOption(hsOption());
		displayOptions.addOption(hdOption());
		displayOptions.addOption(tdOption());
		displayOptions.addOption(ecOption());
		displayOptions.addOption(ddOption());
		displayOptions.addOption(rdOption());
		displayOptions.addOption(clh2Option());
		displayOptions.addOption(ufOption());
		displayOptions.addOption(ddlOption());
		displayOptions.addOption(rstOption());
		displayOptions.addOption(scOption());
		displayOptions.addOption(enOption());
		displayOptions.addOption(gcOption());
		return displayOptions;
	}

	private static Option osOption() {
		return Option.builder(CMD_OS).longOpt("operatingSystem").argName("repeat").numberOfArgs(1).optionalArg(true)
				.hasArgs().desc("显示操作系统信息,间隔2秒.").build();
	}

	private static Option hsOption() {
		return Option.builder(CMD_HS).longOpt("httpStatus").argName("repeat").optionalArg(true).hasArgs()
				.desc("Http服务线程状态,间隔5秒.").build();
	}

	private static Option hdOption() {
		return Option.builder(CMD_HD).longOpt("heapDump").hasArg(false).desc("生成堆转储文件.").build();
	}

	private static Option tdOption() {
		return Option.builder(CMD_TD).longOpt("threadDump").hasArg(false).desc("生成线程转储文件.").build();
	}

	private static Option ecOption() {
		return Option.builder(CMD_EC).longOpt("eraseContent").argName("type").hasArg().optionalArg(false)
				.desc("清空实例数据,保留设计数据,type可选值: bbs,cms,log,processPlatform,message,org或者实体类名.").build();
	}

	private static Option clh2Option() {
		return Option.builder(CMD_CLH2).longOpt("compactLocalH2").desc("压缩本地H2数据库.").build();
	}

	private static Option ddOption() {
		return Option.builder(CMD_DD).longOpt("dumpData").argName("path").hasArg().optionalArg(true)
				.desc("导出数据库服务器的数据转换成json格式保存到本地文件.").build();
	}

	private static Option rdOption() {
		return Option.builder(CMD_RD).longOpt("restoreData").argName("path or date").hasArg()
				.desc("将导出的json格式数据恢复到数据库服务器.").build();
	}

	private static Option ufOption() {
		return Option.builder(CMD_UF).longOpt("updateFile").argName("path").hasArg().desc("升级服务器,升级前请注意备份.").build();
	}

	private static Option ddlOption() {
		return Option.builder(CMD_DDL).longOpt("DataDefinitionLanguage").argName("type").hasArg()
				.desc("导出数据定义语句:建表语句:build,数据库创建:createDB,数据库删除dropDB.").build();
	}

	private static Option rstOption() {
		return Option.builder(CMD_RST).longOpt("restartApplication").argName("name").hasArg()
				.desc("重启指定应用: 应用名称:name, 不带.war").build();
	}

	private static Option scOption() {
		return Option.builder(CMD_SC).longOpt("showCluster").desc("显示集群信息.").build();
	}

	private static Option enOption() {
		return Option.builder(CMD_EN).longOpt("encrypt password text.").argName("text").numberOfArgs(1).hasArg()
				.desc("密码文本加密.").build();
	}

	private static Option deOption() {
		return Option.builder(CMD_DE).longOpt("decrypt password text.").argName("text").numberOfArgs(1).hasArg()
				.desc("密码文本解密.").hasArg().build();
	}

	private static Option gcOption() {
		return Option.builder(CMD_GC).longOpt("jvm garbage collection").hasArg(false).desc("垃圾收集.").build();
	}

	private static Option initResourceFactoryOption() {
		return Option.builder(CMD_INITRESOURCEFACTORY).longOpt("init resource factory").hasArg(false).desc("初始资源工厂.")
				.build();
	}

	private static Option flushConfigOption() {
		return Option.builder(CMD_FLUSHCONFIG).longOpt("flush config").hasArg(false)
				.desc("重置Config对象,不更新externalDataSources,externalStorageSources.").build();
	}

	private static Option regenerateConfigOption() {
		return Option.builder(CMD_REGENERATECONFIG).longOpt("regenerate config").hasArg(false)
				.desc("重新生成Config对象,销毁所有配置对象.").build();
	}

	private void ec(CommandLine cmd) throws Exception {
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
		case "message":
			new EraseContentMessage().execute();
			break;
		case "org":
			new EraseContentOrg().execute();
			break;
		default:
			@SuppressWarnings("unchecked")
			List<String> names = Stream.of(StringUtils.split(type, ","))
					.filter(((List<String>) Config.resource(Config.RESOURCE_CONTAINERENTITYNAMES))::contains)
					.collect(Collectors.toList());
			if (names.isEmpty()) {
				logger.print("unkown parameter:{}.", type);
			} else {
				EraseContentEntity eraseContentEntity = new EraseContentEntity();
				eraseContentEntity.execute(names);
			}
			break;
		}
	}

	private void td(CommandLine cmd) {
		ThreadDump threadDump = new ThreadDump();
		threadDump.execute();
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

	private void rd(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_RD), "");
		RestoreData restoreData = new RestoreData();
		restoreData.execute(path);
	}

	private void hs(CommandLine cmd) {
		final Integer repeat = this.getArgInteger(cmd, CMD_HS, 1);
		HttpStatus httpStatus = new HttpStatus(repeat);
		httpStatus.start();
	}

	private void os(CommandLine cmd) {
		final Integer command = this.getArgInteger(cmd, CMD_OS, 1);
		OperatingSystem operatingSystem = new OperatingSystem(command);
		operatingSystem.start();
	}

	private void hd(CommandLine cmd) throws Exception {
		HeapDump heapDump = new HeapDump();
		heapDump.execute();
	}

	private void uf(CommandLine cmd) throws Exception {
		String path = Objects.toString(cmd.getOptionValue(CMD_UF), "");
		UpdateFile updateFile = new UpdateFile();
		updateFile.execute(path);
	}

	private void ddl(CommandLine cmd) throws Exception {
		String type = Objects.toString(cmd.getOptionValue(CMD_DDL), "");
		Ddl ddl = new Ddl();
		ddl.execute(type);
	}

	private void rst(CommandLine cmd) throws Exception {
		String name = Objects.toString(cmd.getOptionValue(CMD_RST), "");
		RestatWar rst = new RestatWar();
		rst.execute(name);
	}

	private void sc(CommandLine cmd) throws Exception {
		ShowCluster sc = new ShowCluster();
		sc.execute();
	}

	private void en(CommandLine cmd) throws Exception {
		String text = Objects.toString(cmd.getOptionValue(CMD_EN), "");
		Encrypt en = new Encrypt();
		en.execute(text);
	}

	private void de(CommandLine cmd) throws Exception {
		String text = Objects.toString(cmd.getOptionValue(CMD_DE), "");
		Decrypt en = new Decrypt();
		en.execute(text);
	}

	private void gc() {
		GarbageCollection garbageCollection = new GarbageCollection();
		garbageCollection.execute();
	}

	private void initResourceFactory() throws Exception {
		ResourceFactory.destory();
		ResourceFactory.init();
	}

	private void flushConfig() {
		Config.flush();
	}

	private void regenerateConfig() {
		Config.regenerate();
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