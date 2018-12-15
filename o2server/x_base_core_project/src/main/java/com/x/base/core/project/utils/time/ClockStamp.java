package com.x.base.core.project.utils.time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

public class ClockStamp {

	private static ClockStamp INSTANCE;

	private List<Log> logs;

	private Date start;

	private String name;

	private static String FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	public static void INIT(Object name, Object memo) throws Exception {
		synchronized (ClockStamp.class) {
			INSTANCE = new ClockStamp(Objects.toString(name), Objects.toString(memo));
		}
	}

	private ClockStamp(String name, String memo) {
		this.name = name;
		this.start = new Date();
		this.logs = new ArrayList<Log>();
		Log log = new Log(memo, start, 0L);
		this.logs.add(log);
	}

	public static Date STAMP(Object... memos) {
		synchronized (ClockStamp.class) {
			if (null != INSTANCE) {
				List<String> strs = new ArrayList<>();
				for (Object o : memos) {
					strs.add(Objects.toString(o));
				}
				return INSTANCE.stamp(Objects.toString(StringUtils.join(strs, ",")));
			} else {
				System.out.println("ClockStamp not initialized.");
				return null;
			}
		}
	}

	public static Date STAMP() {
		synchronized (ClockStamp.class) {
			if (null != INSTANCE) {
				return INSTANCE.stamp("step " + INSTANCE.logs.size());
			} else {
				System.out.println("ClockStamp not initialized.");
				return null;
			}
		}
	}

	public static void TRACE() {
		synchronized (ClockStamp.class) {
			if (null != INSTANCE) {
				INSTANCE.trace();
			} else {
				System.out.println("ClockStamp not initialized.");
			}
		}
	}

	public static void DESTORY() {
		synchronized (ClockStamp.class) {
			INSTANCE = null;
		}
	}

	private Date stamp(String memo) {
		Date date = new Date();
		long total = date.getTime() - start.getTime();
		Log pre = logs.get(logs.size() - 1);
		long elapsed = date.getTime() - pre.getDate().getTime();
		System.out.println(
				"ClockStamp(" + this.name + ") start at(" + DateFormatUtils.format(date, FORMAT) + "), arrived(" + memo
						+ ") total(" + total + ")ms, form(" + pre.getMemo() + ") elapsed(" + elapsed + ")ms.");
		Log log = new Log(memo, date, elapsed);
		logs.add(log);
		return date;
	}

	private void trace() {
		StringBuffer sb = new StringBuffer();
		sb.append("ClockStamp(" + this.name + ") start at(" + DateFormatUtils.format(start, FORMAT) + ") trace:")
				.append(StringUtils.LF);
		for (Log o : logs) {
			sb.append("(").append(o.getMemo()).append(") elapsed(").append(o.elapsed).append(")ms")
					.append(StringUtils.LF);
		}
		System.out.println(sb);
	}

	protected class Log {

		private String memo;
		private Date date;
		private long elapsed;

		Log(String memo, Date date, long elapsed) {
			this.memo = memo;
			this.date = date;
			this.elapsed = elapsed;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public long getElapsed() {
			return elapsed;
		}

		public void setElapsed(long elapsed) {
			this.elapsed = elapsed;
		}

	}
}
