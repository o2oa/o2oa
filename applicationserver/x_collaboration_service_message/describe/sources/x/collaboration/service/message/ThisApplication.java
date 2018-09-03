package x.collaboration.service.message;

import com.x.base.core.project.Context;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static WsQueue wsQueue;

	public static PushMessageQueue pushMessageQueue;

	public static void init() {
		try {
			wsQueue = new WsQueue(context());
			pushMessageQueue = new PushMessageQueue(context());
			context().startQueue(wsQueue);
			context().startQueue(pushMessageQueue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
