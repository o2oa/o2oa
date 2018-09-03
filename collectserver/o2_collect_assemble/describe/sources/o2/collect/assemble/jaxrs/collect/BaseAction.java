package o2.collect.assemble.jaxrs.collect;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;

abstract class BaseAction extends StandardJaxrsAction {

	private static String MASTER_SECRET = "96ee7e2e0daffd51bac57815";

	private static String APP_KEY = "9aca7cc20fe0cc987cd913ca";

	private static JPushClient jpushClient = null;

	protected JPushClient jpushClient() {
		if (null == jpushClient) {
			synchronized (BaseAction.class) {
				if (null == jpushClient) {
					jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());
				}
			}
		}
		return jpushClient;
	}

}
