package x.collaboration.service.message;

import com.x.base.core.project.Context;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.LoggerFactory;

public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

    public static SmsQueue smsQueue;

	public static WsQueue wsQueue;

	public static PushMessageQueue pushMessageQueue;

	public static void init() {
		try {
			LoggerFactory.setLevel(Config.logLevel().x_collaboration_service_message());
            smsQueue = new SmsQueue(context());
			wsQueue = new WsQueue(context());
			pushMessageQueue = new PushMessageQueue(context());
            context().startQueue(smsQueue);
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
