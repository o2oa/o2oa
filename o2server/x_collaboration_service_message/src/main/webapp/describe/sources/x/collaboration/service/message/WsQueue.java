package x.collaboration.service.message;

import com.google.gson.JsonElement;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

public class WsQueue extends AbstractQueue<JsonElement> {

	private Context context;

	WsQueue(Context context) {
		this.context = context;
	}

	private static Logger logger = LoggerFactory.getLogger(WsQueue.class);

	// private static class SendThread extends Thread {
	// public void run() {
	// while (true) {
	// try {
	// Object o = Queue.take();
	// if (o instanceof StopSignal) {
	// break;
	// }
	// logger.debug("send message:{}.", o);
	// ThisApplication.applications.postQuery(x_collaboration_assemble_websocket.class,
	// "message", o);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }

	@Override
	protected void execute(JsonElement jsonElement) throws Exception {
		logger.debug("send message:{}.", jsonElement);
		context.applications().postQuery(x_collaboration_assemble_websocket.class, "message", jsonElement);

	}
}