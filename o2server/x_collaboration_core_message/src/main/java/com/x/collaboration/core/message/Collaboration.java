package com.x.collaboration.core.message;

import java.util.concurrent.LinkedBlockingQueue;

import com.x.base.core.project.Context;
import com.x.base.core.project.x_collaboration_service_message;

public class Collaboration {

	private static Context context;

	private static LinkedBlockingQueue<BaseMessage> SendQueue = new LinkedBlockingQueue<>();

	public static void send(BaseMessage message) throws Exception {
		SendQueue.put(message);
	}

	public static void start(Context context) {
		Collaboration.context = context;
		SendThread thread = new SendThread();
		thread.start();
	}

	public static void stop() {
		try {
			SendQueue.put(new StopSignal());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class SendThread extends Thread {
		public void run() {
			while (true) {
				try {
					BaseMessage o = SendQueue.take();
					if (o instanceof StopSignal) {
						break;
					}
					context.applications().postQuery(x_collaboration_service_message.class, "message", o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class StopSignal extends BaseMessage {
		public StopSignal() {
			super(null);
		}
	}
}
