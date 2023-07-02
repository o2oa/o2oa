package com.x.program.center;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.program.center.schedule.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static ExecutorService threadPool;

	public static ExecutorService threadPool() {
		return threadPool;
	}

	private static void initThreadPool() {
		int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat(ThisApplication.class.getPackageName() + "-threadpool-%d").build();
		threadPool = new ThreadPoolExecutor(0, maximumPoolSize, 120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
				threadFactory);
	}

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static final CenterQueue centerQueue = new CenterQueue();

	public static final LogQueue logQueue = new LogQueue();

	public static final List<Object> dingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static final List<Object> weLinkSyncOrganizationCallbackRequest = new ArrayList<>();

	public static final List<Object> zhengwuDingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static final List<Object> qiyeweixinSyncOrganizationCallbackRequest = new ArrayList<>();

	public static void init() {
		try {
			initThreadPool();
			CacheManager.init(context.clazz().getSimpleName());
			context().startQueue(centerQueue);
			context().startQueue(logQueue);

			// 政务钉钉拉入同步
			if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())) {
				// 启动同步任务
				context().scheduleLocal(ZhengwuDingdingSyncOrganization.class, Config.zhengwuDingding().getSyncCron());
				// 添加一个强制同步任务
				context().scheduleLocal(ZhengwuDingdingSyncOrganizationTrigger.class,
						Config.zhengwuDingding().getForceSyncCron());
			}
			// 企业微信拉入同步
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& StringUtils.isNotBlank(Config.qiyeweixin().getForceSyncCron())) {
				// 启动同步任务
				context().scheduleLocal(QiyeweixinSyncOrganization.class, Config.qiyeweixin().getSyncCron());
				// 添加一个强制同步任务
				context().scheduleLocal(QiyeweixinSyncOrganizationTrigger.class,
						Config.qiyeweixin().getForceSyncCron());
			}
			// 钉钉同步
			if (BooleanUtils.isTrue(Config.dingding().getEnable())
					&& StringUtils.isNotBlank(Config.dingding().getForceSyncCron())) {
				// 启动同步任务
				context().scheduleLocal(DingdingSyncOrganization.class, Config.dingding().getSyncCron());
				// 添加一个强制同步任务
				context().scheduleLocal(DingdingSyncOrganizationTrigger.class, Config.dingding().getForceSyncCron());
			}
			// WeLink同步
			if (BooleanUtils.isTrue(Config.weLink().getEnable())
					&& StringUtils.isNotBlank(Config.weLink().getForceSyncCron())) {
				// 启动同步任务
				context().scheduleLocal(WeLinkSyncOrganization.class, Config.weLink().getSyncCron());
				// 添加一个强制同步任务
				context().scheduleLocal(WeLinkSyncOrganizationTrigger.class, Config.weLink().getForceSyncCron());
			}
			// 移动办公同步
			if (StringUtils.isNotBlank(Config.andFx().getForceSyncCron())) {
				context().scheduleLocal(AndFxSyncOrganization.class, Config.weLink().getForceSyncCron());
			}

			// 运行间隔由300秒缩减到120秒
			context().scheduleLocal(FireSchedule.class, 180, 120);
			context().scheduleLocal(CleanupCode.class, 10, 60 * 30);
			context().scheduleLocal(Cleanup.class, 10, 60 * 30);
			context().scheduleLocal(CollectPerson.class, 10, 60 * 30);
			context().scheduleLocal(CollectLog.class, 10, 60 * 30);
			// 运行间隔由60秒缩减到30秒
			context().scheduleLocal(TriggerAgent.class, 150, 30);
			/* 行政区域每周更新一次 */
			context().scheduleLocal(Area.class, 300, 60 * 60 * 24 * 7);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
