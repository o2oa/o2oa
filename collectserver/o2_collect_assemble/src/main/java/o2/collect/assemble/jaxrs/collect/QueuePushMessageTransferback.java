//package o2.collect.assemble.jaxrs.collect;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.LinkedBlockingQueue;
//
//import org.apache.commons.codec.digest.DigestUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.bean.NameValuePair;
//import com.x.base.core.project.connection.HttpConnection;
//import com.x.base.core.project.gson.GsonPropertyObject;
//import com.x.base.core.project.gson.XGsonBuilder;
//
//import o2.collect.assemble.Business;
//import o2.collect.assemble.jaxrs.collect.umeng.android.UmengAndroidPushMessage;
//import o2.collect.assemble.jaxrs.collect.umeng.ios.UmengIosPushMessage;
//import o2.collect.assemble.message.BaseMessage;
//import o2.collect.assemble.message.MessageCategory;
//import o2.collect.core.entity.Device;
//import o2.collect.core.entity.Unit;
//
//public class QueuePushMessageTransferback {
//
//	private static Logger logger = LoggerFactory.getLogger(QueuePushMessageTransferback.class);
//
//	private static final String umeng_url = "http://msg.umeng.com/api/send";
//
//	private static final String umeng_user_agent = "Mozilla/5.0";
//
//	private static final String umeng_app_master_secret_ios = "pbok68uz13rzw8si5u8qlrpyqbvf8ul8";
//
//	private static final String umeng_app_master_secret_android = "s8zvvaupgaatbrjnn4ofyicjznf38pmu";
//
//	private static QueuePushMessageTransferback INSTANCE;
//
//	private LinkedBlockingQueue<WiPushMessage> queue;
//
//	private QueuePushMessageTransferback() {
//		this.queue = new LinkedBlockingQueue<WiPushMessage>();
//		ExecuteThread executeThread = new ExecuteThread();
//		executeThread.start();
//	}
//
//	public static void send(WiPushMessage o) throws Exception {
//		INSTANCE.queue.put(o);
//	}
//
//	public static void start() {
//		if (INSTANCE == null) {
//			synchronized (QueuePushMessageTransferback.class) {
//				if (INSTANCE == null) {
//					INSTANCE = new QueuePushMessageTransferback();
//				}
//			}
//		}
//	}
//
//	public static void stop() throws Exception {
//		if (INSTANCE != null) {
//			INSTANCE.queue.put(INSTANCE.new StopSignal());
//		}
//	}
//
//	private class ExecuteThread extends Thread {
//		public void run() {
//			while (true) {
//				try {
//					WiPushMessage o = queue.take();
//					if (o instanceof StopSignal) {
//						break;
//					}
//					execute(o);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	private class StopSignal extends WiPushMessage {
//
//	}
//
//	private void execute(WiPushMessage message) throws Exception {
//		List<Device> devices = new ArrayList<>();
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Business business = new Business(emc);
//			Unit unit = business.validateUnit(message.getUnit(), message.getPassword());
//			if (null == unit) {
//				throw new ExceptionValidateUnitError(message.getUnit());
//			}
//			String accountId = business.account().getWithNameUnit(message.getAccount(), unit.getId());
//			if (StringUtils.isEmpty(accountId)) {
//				throw new ExceptionAccountNotExist(message.getAccount());
//			}
//			List<String> deviceIds = business.device().listWithAccount(accountId);
//			devices = emc.list(Device.class, deviceIds);
//		}
//		logger.info("transfer push message to:{}, title:{}, device count:{}.", message.getAccount(), message.getTitle(),
//				devices.size());
//		for (Device o : devices) {
//			switch (o.getDeviceType()) {
//			case android:
//				androidPush(message, o);
//				break;
//			case ios:
//				iosPush(message, o);
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	private static void androidPush(WiPushMessage message, Device device) {
//		try {
//			UmengAndroidPushMessage push = new UmengAndroidPushMessage();
//			push.setTimestamp(Integer.toString((int) (System.currentTimeMillis() / 1000)));
//			push.setDevice_tokens(device.getName());
//			JsonElement jsonElement = new Gson().fromJson(message.getData(), JsonElement.class);
//			MessageCategory category = BaseMessage.extractCategory(jsonElement);
//			if (null != category) {
//				switch (category) {
//				case notification:
//					push.getPayload().getBody().setTicker(message.getTicker());
//					push.getPayload().getBody().setTitle(message.getTitle());
//					push.getPayload().getBody().setText(message.getText());
//					push.getPayload().getBody().setCustom(jsonElement);
//					break;
//				case dialog:
//					break;
//				case operation:
//					break;
//				default:
//					break;
//				}
//			}
//			List<NameValuePair> heads = new ArrayList<>();
//			heads.add(new NameValuePair("User-Agent", umeng_user_agent));
//			String body = push.toString();
//			String sign = DigestUtils
//					.md5Hex(("POST" + umeng_url + body + umeng_app_master_secret_android).getBytes("utf8"));
//			String address = umeng_url + "?sign=" + sign;
//			String result = HttpConnection.postAsString(address, heads, body);
//			checkResult(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static void iosPush(WiPushMessage message, Device device) {
//		try {
//			UmengIosPushMessage push = new UmengIosPushMessage();
//			push.setTimestamp(Integer.toString((int) (System.currentTimeMillis() / 1000)));
//			push.setDevice_tokens(device.getName());
//			JsonElement jsonElement = new Gson().fromJson(message.getData(), JsonElement.class);
//			MessageCategory category = BaseMessage.extractCategory(jsonElement);
//			if (null != category) {
//				switch (category) {
//				case notification:
//					push.getPayload().getAps().getAlert().setTitle(message.getTitle());
//					// push.getPayload().getBody().setTicker(message.getTicker());
//					// push.getPayload().getBody().setTitle(message.getTitle());
//					// push.getPayload().getBody().setText(message.getText());
//					// push.getPayload().getBody().setCustom(jsonElement);
//					break;
//				case dialog:
//					break;
//				case operation:
//					break;
//				default:
//					break;
//				}
//			}
//			List<NameValuePair> heads = new ArrayList<>();
//			heads.add(new NameValuePair("User-Agent", umeng_user_agent));
//			String body = push.toString();
//			String sign = DigestUtils
//					.md5Hex(("POST" + umeng_url + body + umeng_app_master_secret_ios).getBytes("utf8"));
//			String address = umeng_url + "?sign=" + sign;
//			String result = HttpConnection.postAsString(address, heads, body);
//			checkResult(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static void checkResult(String str) throws Exception {
//		if (StringUtils.isEmpty(str)) {
//			throw new Exception("return string is empty.");
//		}
//		JsonElement jsonElement = XGsonBuilder.instance().fromJson(str, JsonElement.class);
//		if (!jsonElement.isJsonObject()) {
//			throw new Exception("return string:" + str + " is not jsonObject.");
//		}
//		JsonObject jsonObject = jsonElement.getAsJsonObject();
//		if (!jsonObject.has("ret")) {
//			throw new Exception("return object not contain 'ret'.");
//		}
//		String ret = jsonObject.get("ret").getAsString();
//		if (!StringUtils.equalsIgnoreCase("SUCCESS", ret)) {
//			throw new Exception("push message transfer error:" + str + ".");
//		}
//	}
//
//	public static class WiPushMessage extends GsonPropertyObject {
//		private String account;
//		private String unit;
//		private String password;
//		private String data;
//		private String ticker;
//		private String text;
//		private String title;
//
//		public String getAccount() {
//			return account;
//		}
//
//		public void setAccount(String account) {
//			this.account = account;
//		}
//
//		public String getUnit() {
//			return unit;
//		}
//
//		public void setUnit(String unit) {
//			this.unit = unit;
//		}
//
//		public String getPassword() {
//			return password;
//		}
//
//		public void setPassword(String password) {
//			this.password = password;
//		}
//
//		public String getData() {
//			return data;
//		}
//
//		public void setData(String data) {
//			this.data = data;
//		}
//
//		public String getTicker() {
//			return ticker;
//		}
//
//		public void setTicker(String ticker) {
//			this.ticker = ticker;
//		}
//
//		public String getText() {
//			return text;
//		}
//
//		public void setText(String text) {
//			this.text = text;
//		}
//
//		public String getTitle() {
//			return title;
//		}
//
//		public void setTitle(String title) {
//			this.title = title;
//		}
//	}
//
//}