package com.x.program.init;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.StringTools;

public class Missions {

	public static final String STATUS_WAITING = "waiting";
	public static final String STATUS_RUNNING = "running";
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILURE = "failure";

	private static final ThreadPoolExecutor THREADPOOLEXECUTOR = new ThreadPoolExecutor(1, 1, 300, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(1000),
			new ThreadFactoryBuilder().setNameFormat(Missions.class.getName() + "-threadpool-%d").build());

	private Missions() {
		// nothing
	}

	private static Messages messages;

	private static String status = STATUS_WAITING;

	public static boolean isEmpty() {
		return stream().filter(Objects::nonNull).count() == 0;
	}

	public static void execute() throws InterruptedException, ExecutionException {

		messages = new Messages();

		status = STATUS_RUNNING;

		Future<?> future = THREADPOOLEXECUTOR
				.submit(() -> stream().filter(Objects::nonNull).forEach(o -> o.execute(messages)));

		future.get();

		status = STATUS_SUCCESS;

	}

	private static Stream<Mission> stream() {
		return Stream.<Mission>of(ThisApplication.getMissionSetSecret(),
				ThisApplication.getMissionExternalDataSources(), ThisApplication.getMissionRestore());
	}

	public interface Mission {

		public void execute(Messages messages);

	}

	public static class Messages extends ArrayList<String> {

		private static final long serialVersionUID = 8436156938958258642L;

		private String head;

		private String failureMessage = "";

		private Boolean failure = false;

		public Messages head(String head, Object... objs) {
			this.head = StringTools.format(head, objs);
			return this;
		}

		public Messages msg(String message, Object... objs) {
			this.add(head + ":" + StringTools.format(message, objs));
			return this;
		}

		public void err(String message, Object... objs) {
			String txt = StringTools.format(message, objs);
			this.add(head + ":" + txt);
			this.failureMessage = txt;
			this.failure = true;
		}

		public String getHead() {
			return head;
		}

		public void setHead(String head) {
			this.head = head;
		}

		public String getFailureMessage() {
			return failureMessage;
		}

		public void setFailureMessage(String failureMessage) {
			this.failureMessage = failureMessage;
		}

		public Boolean getFailure() {
			return failure;
		}

		public void setFailure(Boolean failure) {
			this.failure = failure;
		}

	}

	public static ExecuteStatus getExecuteStatus() {
		ExecuteStatus executeStatus = new ExecuteStatus();
		boolean running = THREADPOOLEXECUTOR.getActiveCount() > 0 || (!THREADPOOLEXECUTOR.getQueue().isEmpty());
		if (running) {
			executeStatus.setStatus(STATUS_RUNNING);
		} else if ((null != messages) && BooleanUtils.isTrue(messages.getFailure())) {
			executeStatus.setStatus(STATUS_FAILURE);
		} else {
			executeStatus.setStatus(status);
		}
		executeStatus.setMessages(messages);
		return executeStatus;
	}

	public static class ExecuteStatus extends GsonPropertyObject {

		private static final long serialVersionUID = -7265462641759175622L;

		private String status;

		private Messages messages;

		private String failureMessage;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getFailureMessage() {
			return failureMessage;
		}

		public void setFailureMessage(String failureMessage) {
			this.failureMessage = failureMessage;
		}

		public Messages getMessages() {
			return messages;
		}

		public void setMessages(Messages messages) {
			this.messages = messages;
		}

	}

}
