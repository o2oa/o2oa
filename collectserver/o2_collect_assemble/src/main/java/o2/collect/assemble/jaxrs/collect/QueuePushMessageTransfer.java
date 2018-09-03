package o2.collect.assemble.jaxrs.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

public class QueuePushMessageTransfer {

	private static Logger logger = LoggerFactory.getLogger(QueuePushMessageTransfer.class);

	private JPushClient jpushClient;

	private static String MASTER_SECRET = "96ee7e2e0daffd51bac57815";

	private static String APP_KEY = "9aca7cc20fe0cc987cd913ca";

	// private static final String umeng_url = "http://msg.umeng.com/api/send";
	//
	// private static final String umeng_user_agent = "Mozilla/5.0";
	//
	// private static final String umeng_app_master_secret_ios =
	// "pbok68uz13rzw8si5u8qlrpyqbvf8ul8";
	//
	// private static final String umeng_app_master_secret_android =
	// "s8zvvaupgaatbrjnn4ofyicjznf38pmu";

	private static QueuePushMessageTransfer INSTANCE;

	private LinkedBlockingQueue<WiPushMessage> queue;

	private QueuePushMessageTransfer() {
		this.queue = new LinkedBlockingQueue<WiPushMessage>();
		jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, ClientConfig.getInstance());
		ExecuteThread executeThread = new ExecuteThread();
		executeThread.start();
	}

	public static void send(WiPushMessage o) throws Exception {
		INSTANCE.queue.put(o);
	}

	public static void start() {
		if (INSTANCE == null) {
			synchronized (QueuePushMessageTransfer.class) {
				if (INSTANCE == null) {
					INSTANCE = new QueuePushMessageTransfer();
				}
			}
		}
	}

	public static void stop() throws Exception {
		if (INSTANCE != null) {
			INSTANCE.queue.put(INSTANCE.new StopSignal());
		}
	}

	private class ExecuteThread extends Thread {
		public void run() {
			while (true) {
				try {
					WiPushMessage o = queue.take();
					if (o instanceof StopSignal) {
						break;
					}
					execute(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class StopSignal extends WiPushMessage {

	}

	private void execute(WiPushMessage message) throws Exception {
		List<Device> devices = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Unit unit = business.validateUnit(message.getUnit(), message.getPassword());
			if (null == unit) {
				throw new ExceptionValidateUnitError(message.getUnit());
			}
			String accountId = business.account().getWithNameUnit(message.getAccount(), unit.getId());
			if (StringUtils.isEmpty(accountId)) {
				throw new ExceptionAccountNotExist(message.getAccount());
			}
			List<String> deviceIds = business.device().listWithAccount(accountId);
			devices = emc.list(Device.class, deviceIds);
		}
		logger.info("transfer push message to:{}, title:{}, device count:{}.", message.getAccount(), message.getTitle(),
				devices.size());
		for (Device o : devices) {
			PushPayload pushPayload = PushPayload.newBuilder().setPlatform(Platform.all())
					.setAudience(Audience.registrationId(o.getName()))
					.setNotification(Notification.alert(message.getTitle())).build();
			try {
				PushResult result = jpushClient.sendPush(pushPayload);
				System.out.println(result);
			} catch (APIConnectionException e) {
				e.printStackTrace();
			} catch (APIRequestException e) {
				e.printStackTrace();
			}
		}
	}

	public static class WiPushMessage extends GsonPropertyObject {
		private String account;
		private String unit;
		private String password;
		private String data;
		private String ticker;
		private String text;
		private String title;

		public String getAccount() {
			return account;
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getTicker() {
			return ticker;
		}

		public void setTicker(String ticker) {
			this.ticker = ticker;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

}