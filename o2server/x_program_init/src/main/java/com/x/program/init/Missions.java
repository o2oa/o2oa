package com.x.program.init;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class Missions {

	private static final ThreadPoolExecutor THREADPOOLEXECUTOR = new ThreadPoolExecutor(1, 1, 120, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(1000),
			new ThreadFactoryBuilder().setNameFormat(Missions.class.getName() + "-threadpool-%d").build());

	private Missions() {
		// nothing
	}

	private static Messages messages;

	public static boolean isEmpty() {
		return stream().filter(Objects::nonNull).count() == 0;
	}

	public static void execute() throws InterruptedException, ExecutionException {

		messages = new Messages();

		Future<?> future = THREADPOOLEXECUTOR
				.submit(() -> stream().filter(Objects::nonNull).forEach(o -> o.execute(messages)));
		future.get();

	}

	private static Stream<Mission> stream() {
		return Stream.<Mission>of(ThisApplication.getMissionH2Upgrade(), ThisApplication.getMissionSetSecret(),
				ThisApplication.getMissionExternalDataSources(), ThisApplication.getMissionRestore());
	}

	public interface Mission {

		public void execute(Messages messages);

	}

	public static class Messages extends ArrayList<String> {

		private static final long serialVersionUID = 8436156938958258642L;

		private String head;

		public Messages head(String head, Object... objs) {
			this.head = StringTools.format(head, objs);
			return this;
		}

		public Messages msg(String message, Object... objs) {
			this.add(head + ":" + StringTools.format(message, objs));
			return this;
		}

	}

	public static ExecuteStatus getExecuteStatus() {
		ExecuteStatus status = new ExecuteStatus();
		status.setRunning(THREADPOOLEXECUTOR.getActiveCount() > 0 || (!THREADPOOLEXECUTOR.getQueue().isEmpty()));
		status.setMessages(messages);
		return status;
	}

	public static class ExecuteStatus extends GsonPropertyObject {

		private static final long serialVersionUID = -7265462641759175622L;

		private Boolean running;

		private Messages messages;

		public Boolean getRunning() {
			return running;
		}

		public void setRunning(Boolean running) {
			this.running = running;
		}

		public Messages getMessages() {
			return messages;
		}

		public void setMessages(Messages messages) {
			this.messages = messages;
		}

	}

}
