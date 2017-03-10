package x.collaboration.service.message;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.x_collaboration_assemble_websocket;

public class WsConnector {

	private static Logger logger = LoggerFactory.getLogger(WsConnector.class);

	private static LinkedBlockingQueue<Object> Queue = new LinkedBlockingQueue<>();

	public static void send(JsonElement o) throws Exception {
		Queue.put(o);
	}

	public static void start() {
		SendThread thread = new SendThread();
		thread.start();
	}

	public static void stop() {
		try {
			Queue.put(new StopSignal());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class SendThread extends Thread {
		public void run() {
			while (true) {
				try {
					Object o = Queue.take();
					if (o instanceof StopSignal) {
						break;
					}
					logger.debug("send message:{}.", o);
					ThisApplication.applications.postQuery(x_collaboration_assemble_websocket.class, "message", o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class StopSignal {

	}
}