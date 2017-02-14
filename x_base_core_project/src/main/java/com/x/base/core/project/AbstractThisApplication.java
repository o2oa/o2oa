package com.x.base.core.project;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.enhance.PCRegistry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.application.Applications;
import com.x.base.core.bean.NameValuePair;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
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
	private static ScheduledExecutorService scheduler;

	protected static void scheduleWithFixedDelay(Runnable runnable, int initialDelay, int delay) {
		scheduler.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.SECONDS);
	}

	protected static void schedule(Runnable runnable, int initialDelay) {
		scheduler.schedule(runnable, initialDelay, TimeUnit.SECONDS);
	}

	public static void initBefore(Class<?> clazz, String context, String path) throws Exception {
		AbstractThisApplication.clazz = clazz;
		AbstractThisApplication.context = context;
		AbstractThisApplication.path = path;
		AbstractThisApplication.token = UUID.randomUUID().toString();
		/* 必须先将ThisApplication.initialized = true，否则Center无法getAvaliable */
		AbstractThisApplication.initialized = true;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		System.out.println(context + " start completed.");
	}

	public static void destroyAfter() throws Exception {
		try {
			scheduler.shutdownNow();
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

}