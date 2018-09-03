package test.o2.collect.assemble;

import org.junit.Test;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

public class TestClientJiguang {

	private static String MASTER_SECRET = "96ee7e2e0daffd51bac57815";

	private static String APP_KEY = "9aca7cc20fe0cc987cd913ca";

	@Test
	public void test() {

		JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());

		// For push, all you need do is to build PushPayload object.
		PushPayload payload = buildPushObject_all_alias_alert();

		try {
			PushResult result = jpushClient.sendPush(payload);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static PushPayload buildPushObject_all_alias_alert() {
		return PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.alias("141fe1da9e86b26bdf1"))
				.setNotification(Notification.alert("aaaaa")).build();
	}

}
