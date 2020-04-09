package com.x.server.console.action;

import java.io.File;
import java.lang.Thread.State;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;

public class ActionStack extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionStack.class);

	public boolean execute(Integer count, Integer interval, String password) {
		try {
			if (!StringUtils.equals(Config.token().getPassword(), password)) {
				logger.print("password not match.");
				return false;
			}
			Map<Long, Integer> state_new = new HashMap<>();
			Map<Long, Integer> state_runable = new HashMap<>();
			Map<Long, Integer> state_blocked = new HashMap<>();
			Map<Long, Integer> state_waiting = new HashMap<>();
			Map<Long, Integer> state_timed_waiting = new HashMap<>();
			Map<Long, Integer> state_terminated = new HashMap<>();
			File startFile = null;
			for (int i = 1; i <= count; i++) {
				Date date = new Date();
				StringBuffer buffer = new StringBuffer();
				for (Thread thread : ThreadUtils.getAllThreads()) {
					this.state(thread, state_new, state_runable, state_blocked, state_waiting, state_timed_waiting,
							state_terminated);
					this.dump(buffer, thread);
				}
				File file = new File(Config.dir_logs(true), this.fileName(i, count, date));
				if (i == 1) {
					startFile = file;
				}
				if (i != count) {
					FileUtils.write(file, buffer, DefaultCharset.charset_utf_8);
					Thread.sleep(interval);
				} else {
					buffer.append(System.lineSeparator()).append("Thread state statistics:");
					buffer.append(" NEW(" + state_new.size() + "),");
					buffer.append(" RUNABLE(" + state_runable.size() + "),");
					buffer.append(" blocked(" + state_blocked.size() + "),");
					buffer.append(" waiting(" + state_waiting.size() + "),");
					buffer.append(" timed_waiting(" + state_timed_waiting.size() + "),");
					buffer.append(" terminated(" + state_terminated.size() + ").");
					buffer.append(System.lineSeparator());
					this.writeState(buffer, state_new, State.NEW, count);
					this.writeState(buffer, state_runable, State.RUNNABLE, count);
					this.writeState(buffer, state_blocked, State.BLOCKED, count);
					this.writeState(buffer, state_waiting, State.WAITING, count);
					this.writeState(buffer, state_timed_waiting, State.TIMED_WAITING, count);
					this.writeState(buffer, state_terminated, State.TERMINATED, count);
					FileUtils.write(file, buffer, DefaultCharset.charset_utf_8);
					logger.print("stack dump thread to files: {} - {}.", startFile.getAbsolutePath(),
							file.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void writeState(StringBuffer buffer, Map<Long, Integer> map, State state, Integer count) {
		buffer.append(state).append(", count:").append(map.size()).append(".").append(System.lineSeparator());
		map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder())).forEach(o -> {
			buffer.append("\t\t").append(o.getKey()).append(":").append(o.getValue()).append("/").append(count)
					.append(System.lineSeparator());
		});
	}

	private void state(Thread thread, Map<Long, Integer> state_new, Map<Long, Integer> state_runable,
			Map<Long, Integer> state_blocked, Map<Long, Integer> state_waiting, Map<Long, Integer> state_timed_waiting,
			Map<Long, Integer> state_terminated) {
		Map<Long, Integer> state;
		switch (thread.getState()) {
		case NEW:
			state = state_new;
			break;
		case RUNNABLE:
			state = state_runable;
			break;
		case BLOCKED:
			state = state_blocked;
			break;
		case WAITING:
			state = state_waiting;
			break;
		case TIMED_WAITING:
			state = state_timed_waiting;
			break;
		case TERMINATED:
			state = state_terminated;
			break;
		default:
			state = state_terminated;
			break;
		}
		state.compute(thread.getId(), (k, v) -> {
			if (null == v) {
				return 1;
			}
			return v + 1;
		});
	}

	private void dump(StringBuffer buffer, Thread thread) {
		this.title(buffer, thread);
		for (StackTraceElement stackTrace : thread.getStackTrace()) {
			this.stackTrace(buffer, stackTrace);
		}
	}

	private void title(StringBuffer buffer, Thread thread) {
		buffer.append(String.format("id:%s, state:%s, threadGroup:%s, priority:%d.", thread.getId(), thread.getState(),
				thread.getThreadGroup(), thread.getPriority())).append(System.lineSeparator());
	}

	private void stackTrace(StringBuffer buffer, StackTraceElement stackTraceElement) {
		buffer.append("\t\t").append(stackTraceElement.toString()).append(System.lineSeparator());
	}

	private String fileName(int idx, int count, Date date) {
		return "stack_" + DateTools.format(date, "yyyyMMddHHmmssSSS") + "_"
				+ StringUtils.repeat('0', ((count + "").length()) - ((idx + "").length())) + idx + "_" + count + ".txt";
	}

}