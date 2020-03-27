package com.x.server.console.action;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ActionDebugDetailDisplay extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionShowMemory.class);

	private static Options options;

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(String... args) {
		try {
			this.init();
			new Thread() {
				public void run() {
					try {
						CommandLineParser parser = new DefaultParser();
						CommandLine cmd = parser.parse(options(), args);
						if (cmd.hasOption("ppe")) {
							ppe(cmd);
						} else if (cmd.hasOption("os")) {
							os(cmd);
						} else {
							HelpFormatter formatter = new HelpFormatter();
							formatter.printHelp("ddd (Debug Detail Display)", options);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Options options() {
		if (null == options) {
			options = new Options();
			options.addOption(ppexeOption());
			options.addOption(osOption());
		}
		return options;
	}

	private Option ppexeOption() {
		Option opt = Option.builder("ppexe").desc("显示流程平台执行线程状态.").build();
		return opt;
	}

	private Option osOption() {
		Option opt = Option.builder("os").argName("repeat").optionalArg(true).hasArgs().type(Integer.class)
				.desc("显示操作系统信息.").build();
		return opt;
	}

	private void ppe(CommandLine cmd) throws Exception {
		ExecutorService[] executorServices = Config.resource_node_processPlatformExecutors();
		List<String> list = new ArrayList<>();
		for (int i = 0; i < executorServices.length; i++) {
			ExecutorService service = executorServices[i];
			ThreadPoolExecutor executor = (ThreadPoolExecutor) service;
			BlockingQueue<Runnable> queue = executor.getQueue();
			list.add("processPlatform executorServices[" + i + "] completed: " + executor.getCompletedTaskCount()
					+ ", block: " + queue.size() + ".");
			if (!queue.isEmpty()) {
				List<String> os = new ArrayList<>();
				for (Runnable o : queue) {
					os.add(o.getClass().toString());
				}
				list.add("\t┕blocking: " + StringUtils.join(os, ",") + ".");
			}
		}
		System.out.println(StringUtils.join(list, StringUtils.LF));
	}

	private void os(CommandLine cmd) throws Exception {
		Integer repeat = 10;
		String r = cmd.getOptionValue("os", "10");
		if (NumberUtils.isParsable(r)) {
			repeat = NumberUtils.toInt(r);
		}
		if (repeat < 1 || repeat > 100) {
			repeat = 10;
		}
		// final Integer interval_adjust = Math.min(Math.max(interval, 1), 20);
		// final Integer repeat_repeat = Math.min(Math.max(repeat, 1), 200);
		final Integer rpt = repeat;
		new Thread() {
			public void run() {
				OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
				try {
					for (int i = 0; i < rpt; i++) {
						StringBuffer buffer = new StringBuffer();
						buffer.append("cpu:" + bean.getAvailableProcessors())
								.append(", process load:" + percent(bean.getProcessCpuLoad()))
								.append(", system load:" + percent(bean.getSystemCpuLoad()))
								.append(". memory:").append(bean.getTotalPhysicalMemorySize() / (1024 * 1024))
								.append("m, free:").append(bean.getFreePhysicalMemorySize() / (1024 * 1024))
								.append("m, committed virtual:")
								.append(bean.getCommittedVirtualMemorySize() / (1024 * 1024)).append("m.");
						System.out.println(buffer.toString());
						Thread.sleep(3000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private String percent(Double d) {
		return new Double((d * 100)).intValue() + "%";
	}

}