package com.x.base.core.project;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.enhance.PCRegistry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.DefaultCharset;
import com.x.base.core.application.Applications;
import com.x.base.core.bean.NameValuePair;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.connection.HttpConnection;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataMappings;
import com.x.base.core.project.server.StorageMappings;
import com.x.base.core.utils.ListTools;

public abstract class AbstractThisApplication {
	/* 应用的磁盘路径 */
	public static volatile String path;
	/* 上下文根 */
	public static volatile String context;
	/* 应用类 */
	public static Class<?> clazz;
	/* 随机令牌 */
	public static volatile String token;
	/* Applications资源 */
	public static volatile Applications applications;
	/* Storage资源 */
	public static volatile StorageMappings storageMappings;
	/* 是否已经初始化完成 */
	public static volatile boolean initialized;
	/* 用于执行定时任务的执行服务 */
	private static ScheduledExecutorService scheduledExecutorService;
	/* 用于执行单机运行的任务 */
	private static List<Job> timerJobs;
	// /* 用于执行统一排程的定时任务 */
	private static List<Job> scheduleJobs;

	protected static void timerWithFixedDelay(TimerTask timerTask, int initialDelay, int delay) throws Exception {
		if (null == timerTask) {
			throw new Exception("timerWithFixedDelay task can not be null.");
		}
		Job o = new Job();
		o.setTimerTask(timerTask);
		o.setInitialDelay(initialDelay);
		o.setDelay(delay);
		timerJobs.add(o);
		scheduledExecutorService.scheduleWithFixedDelay(timerTask, initialDelay, delay, TimeUnit.SECONDS);
	}

	protected static void timer(TimerTask timerTask, int initialDelay) throws Exception {
		if (null == timerTask) {
			throw new Exception("timer task can not be null.");
		}
		scheduledExecutorService.schedule(timerTask, initialDelay, TimeUnit.SECONDS);
	}

	protected static void schedule(TimerTask timerTask, int initialDelay, int delay) throws Exception {
		/* 统一排程任务需要延时90秒,等待instrument启动,每次间隔不能少于5分钟 */
		if (null == timerTask) {
			throw new Exception("schedule task can not be null.");
		}
		if (initialDelay < 90) {
			initialDelay = 90;
		}
		if (delay < 300) {
			delay = 300;
		}
		Job o = new Job();
		o.setTimerTask(timerTask);
		o.setInitialDelay(initialDelay);
		o.setDelay(delay);
		scheduleJobs.add(o);
	}

	public static void initBefore(Class<?> clazz, String context, String path) throws Exception {
		AbstractThisApplication.clazz = clazz;
		AbstractThisApplication.context = context;
		AbstractThisApplication.path = path;
		AbstractThisApplication.token = UUID.randomUUID().toString();
		/* 必须先将ThisApplication.initialized = true，否则Center无法getAvaliable */
		AbstractThisApplication.initialized = true;
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduleJobs = new ArrayList<Job>();
		timerJobs = new ArrayList<Job>();
		/** 生成默认的schedule,timer轮询任务 */
		scheduleTimeReportToInstrument(clazz);
		System.out.println(context + " start completed.");
	}

	private static void scheduleTimeReportToInstrument(Class<?> clazz) {
		/* 跳过x_program_center 不然x_program_center无法单独启动 */
		if (clazz.equals(x_program_center.class)) {
			return;
		}
		try {
			timerWithFixedDelay(new TimerTask() {
				public void run() {
					for (Job o : timerJobs) {
						/* 报告timer任务 */
						try {
							try {
								String url = "timer/node/" + Config.node() + "/application/"
										+ URLEncoder.encode(clazz.getName(), DefaultCharset.name);
								url += "/token/" + URLEncoder.encode(token, DefaultCharset.name);
								url += "/timertask/"
										+ URLEncoder.encode(o.getTimerTask().getClass().getName(), DefaultCharset.name);
								url += "/initialdelay/" + o.getInitialDelay();
								url += "/delay/" + o.getDelay();
								WrapOutBoolean wrap = applications.getQuery(x_instrument_service_express.class, url,
										WrapOutBoolean.class);
								if (null == wrap) {
									throw new Exception("can not get report for url:" + url);
								}
							} catch (Exception e) {
								throw new Exception(clazz.getName() + " error on timer job:" + o + ".", e);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					/* 检查schdule任务 */
					for (Job o : scheduleJobs) {
						try {
							try {
								String url = "schedule/node/" + Config.node() + "/application/"
										+ URLEncoder.encode(clazz.getName(), DefaultCharset.name);
								url += "/token/" + URLEncoder.encode(token, DefaultCharset.name);
								url += "/timertask/"
										+ URLEncoder.encode(o.getTimerTask().getClass().getName(), DefaultCharset.name);
								url += "/initialdelay/" + o.getInitialDelay();
								url += "/delay/" + o.getDelay();
								WrapOutBoolean wrap = applications.getQuery(x_instrument_service_express.class, url,
										WrapOutBoolean.class);
								if (null == wrap) {
									throw new Exception("can not get check for url:" + url);
								}
								if (BooleanUtils.isTrue(wrap.getValue())) {
									timer(o.getTimerTask(), 5);
								}
							} catch (Exception e) {
								throw new Exception(clazz.getName() + " error on schedule job:" + o + ".", e);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 90, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroyAfter() throws Exception {
		try {
			scheduledExecutorService.shutdownNow();
			EntityManagerContainerFactory.close();
			PCRegistry.deRegister(JpaObject.class.getClassLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void initDatasFromCenters() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> containerEntities = (List<String>) FieldUtils.readStaticField(clazz, "containerEntities");
		if (ListTools.isNotEmpty(containerEntities)) {
			System.out.println(context + " loading datas.");
			DataMappings dataMappings = getFromCenter("/jaxrs/datamappings", DataMappings.class);
			EntityManagerContainerFactory.init(path, dataMappings);
		}
	}

	protected static void initStoragesFromCenters() throws Exception {
		@SuppressWarnings("unchecked")
		List<StorageType> usedStorageTypes = (List<StorageType>) FieldUtils.readStaticField(clazz, "usedStorageTypes");
		// if (ListTools.isNotEmpty(usedStorageTypes)) {
		// System.out.println(context + " loading storages.");
		// AbstractThisApplication.storageMappings =
		// getFromCenter("/jaxrs/storagemappings", StorageMappings.class);
		// }
		AbstractThisApplication.storageMappings = getFromCenter("/jaxrs/storagemappings", StorageMappings.class);
	}

	public static String getCenterUrl() throws Exception {
		String url = "http://" + Config.nodes().primaryCenterNode() + ":" + Config.centerServer().getPort() + "/"
				+ x_program_center.class.getSimpleName();
		return url;
	}

	public static <T> T getFromCenter(String path, Class<T> cls) throws Exception {
		try {
			EffectivePerson effectivePerson = EffectivePerson.cipher(Config.token().getCipher());
			String url = AbstractThisApplication.getCenterUrl() + path;
			List<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair(HttpToken.X_Token, effectivePerson.getToken()));
			JsonElement element = HttpConnection.getAsObject(url, heads, JsonElement.class);
			if (element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				if (obj.has("data")) {
					return (XGsonBuilder.pureGsonDateFormated()).fromJson(obj.get("data"), cls);
				}
			}
			throw new Exception("can not read getFormCenter from center.");
		} catch (Exception e) {
			throw new Exception(AbstractThisApplication.clazz + " getFormCenter serror.", e);
		}
	}

	public static class Job extends GsonPropertyObject {

		private TimerTask timerTask;
		private Integer initialDelay;
		private Integer delay;

		public Integer getInitialDelay() {
			return initialDelay;
		}

		public void setInitialDelay(Integer initialDelay) {
			this.initialDelay = initialDelay;
		}

		public Integer getDelay() {
			return delay;
		}

		public void setDelay(Integer delay) {
			this.delay = delay;
		}

		public TimerTask getTimerTask() {
			return timerTask;
		}

		public void setTimerTask(TimerTask timerTask) {
			this.timerTask = timerTask;
		}

	}

}