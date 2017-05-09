package com.x.base.core.project;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.enhance.PCRegistry;

import com.x.base.core.cache.ClearCacheRequest;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.project.clock.ClockScheduleTask;
import com.x.base.core.project.clock.ClockTimerTask;
import com.x.base.core.project.clock.ReportToCenter;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.DataMappings;
import com.x.base.core.project.server.StorageMappings;
import com.x.base.core.utils.ListTools;

public class Context {

	/** 从servletContext中抽取context */
	public static Context fromServletContext(ServletContext servletContext) throws Exception {
		Object o = servletContext.getAttribute(Context.class.getName());
		if (null == o) {
			throw new Exception("can not get context form servletContext.");
		} else {
			return (Context) o;
		}
	}

	/** 从servletRequest中抽取context */
	public static Context fromServletRequest(ServletRequest servletRequest) throws Exception {
		Object o = servletRequest.getServletContext().getAttribute(Context.class.getName());
		if (null == o) {
			throw new Exception("can not get context form servletRequest.");
		} else {
			return (Context) o;
		}
	}

	/* 应用的磁盘路径 */
	private volatile String path;

	public String path() {
		return this.path;
	}

	/* 上下文 */
	private volatile String servletContextName;

	public String servletContextName() {
		return this.servletContextName;
	}

	/* 上下文根 */
	private volatile ServletContext servletContext;

	public ServletContext servletContext() {
		return this.servletContext;
	}

	/* 应用类 */
	private Class<?> clazz;

	public Class<?> clazz() {
		return this.clazz;
	}

	/* 随机令牌 */
	private volatile String token;

	public String token() {
		return this.token;
	}

	/* Applications资源 */
	private volatile Applications applications;

	public Applications applications() {
		synchronized (this) {
			return this.applications;
		}
	}

	/* Storage资源 */
	private volatile StorageMappings storageMappings;

	public StorageMappings storageMappings() {
		return this.storageMappings;
	}

	/* 是否已经初始化完成 */
	private volatile boolean initialized;

	public boolean initialized() {
		return this.initialized;
	}

	/* 应用的权重 */
	private volatile Integer weight;

	public Integer weight() {
		return this.weight;
	}

	private Boolean sslEnable;

	public Boolean sslEnable() {
		return this.sslEnable;
	}

	/* 用于执行定时任务的执行服务 */
	private ScheduledExecutorService scheduledExecutorService;
	/* 用于执行单机运行的任务 */
	private List<TimerJob> timerJobs;

	public List<TimerJob> timerJobs() {
		return this.timerJobs;
	}

	/* 用于执行统一排程的定时任务 */
	private List<ScheduleJob> scheduleJobs;

	public List<ScheduleJob> scheduleJobs() {
		return this.scheduleJobs;
	}

	private AbstractQueue<ClearCacheRequest> clearCacheRequestQueue;

	public AbstractQueue<ClearCacheRequest> clearCacheRequestQueue() {
		return this.clearCacheRequestQueue;
	}

	/* 队列 */
	private List<AbstractQueue<?>> queues;

	private Context() {
		this.token = UUID.randomUUID().toString();
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		this.scheduleJobs = new ArrayList<ScheduleJob>();
		this.timerJobs = new ArrayList<TimerJob>();
		this.applications = new Applications();
		this.queues = new ArrayList<AbstractQueue<?>>();
	}

	public static Context concrete(ServletContextEvent servletContextEvent,
			AbstractQueue<ClearCacheRequest> clearCacheRequestQueue) throws Exception {
		Context context = concrete(servletContextEvent);
		if (null != clearCacheRequestQueue) {
			context.clearCacheRequestQueue = clearCacheRequestQueue;
			context.startQueue(clearCacheRequestQueue);
		}
		return context;
	}

	public static Context concrete(ServletContextEvent servletContextEvent) throws Exception {
		ServletContext servletContext = servletContextEvent.getServletContext();
		Context context = new Context();
		context.path = servletContext.getRealPath("");
		context.servletContext = servletContext;
		context.servletContextName = servletContext.getServletContextName();
		context.clazz = Class.forName("com.x.base.core.project." + context.servletContextName);
		context.weight = Config.currentNode().getApplication().weight(context.clazz);
		context.sslEnable = Config.currentNode().getApplication().getSslEnable();
		context.initDatasFromCenters();
		context.initStoragesFromCenters();
		ReportToCenter reportToCenter = new ReportToCenter(context);
		/* 同步运行一次 */
		reportToCenter.execute();
		/* 再异步加入到定时循环中 */
		context.timer(reportToCenter, 30, 45);
		context.initialized = true;
		servletContext.setAttribute(context.getClass().getName(), context);
		return context;
	}

	public void timer(ClockTimerTask task, int initialDelay, int delay) throws Exception {
		if (null == task) {
			throw new Exception("timerWithFixedDelay task can not be null.");
		}
		TimerJob o = new TimerJob(task, initialDelay, delay);
		timerJobs.add(o);
		scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.SECONDS);
	}

	public void timer(TimerTask task, int initialDelay) throws Exception {
		if (null == task) {
			throw new Exception("timer task can not be null.");
		}
		scheduledExecutorService.schedule(task, initialDelay, TimeUnit.SECONDS);
	}

	public <T extends ClockScheduleTask> void schedule(Class<T> clz, String cron) throws Exception {
		/* 统一排程任务需要延时90秒,等待instrument启动,每次间隔不能少于5分钟 */
		if (null == clz) {
			throw new Exception("schedule task can not be null.");
		}
		ScheduleJob o = new ScheduleJob(clz, cron);
		scheduleJobs.add(o);
	}

	public void startQueue(AbstractQueue<?> queue) {
		queues.add(queue);
		queue.start();
	}

	public void loadApplications() {
		try {
			synchronized (this) {
				ActionResponse response = CipherConnectionAction.get(Config.x_program_centerUrlRoot() + "applications");
				this.applications = response.getData(Applications.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDatasFromCenters() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> containerEntities = (List<String>) FieldUtils.readStaticField(clazz, "containerEntities");
		if (ListTools.isNotEmpty(containerEntities)) {
			System.out.println(servletContextName + " loading datas.");
			DataMappings dataMappings = null;
			do {
				try {
					ActionResponse rep = CipherConnectionAction.get(Config.x_program_centerUrlRoot() + "datamappings");
					dataMappings = rep.getData(DataMappings.class);
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(5000);
				}
			} while (null == dataMappings);
			EntityManagerContainerFactory.init(path, dataMappings);
		}
	}

	private void initStoragesFromCenters() throws Exception {
		@SuppressWarnings("unchecked")
		List<StorageType> usedStorageTypes = (List<StorageType>) FieldUtils.readStaticField(clazz, "usedStorageTypes");
		if (ListTools.isNotEmpty(usedStorageTypes)) {
			StorageMappings storageMappings = null;
			do {
				try {
					ActionResponse rep = CipherConnectionAction
							.get(Config.x_program_centerUrlRoot() + "storagemappings");
					storageMappings = rep.getData(StorageMappings.class);
				} catch (Exception e) {
					e.printStackTrace();
					Thread.sleep(5000);
				}
			} while (null == storageMappings);
			this.storageMappings = storageMappings;
		}
	}

	public class TimerJob extends GsonPropertyObject {

		public TimerJob(ClockTimerTask clockTimerTask, Integer initialDelay, Integer delay) {
			this.clockTimerTask = clockTimerTask;
			this.initialDelay = initialDelay;
			this.delay = delay;
			this.timerTaskClassName = clockTimerTask.getClass().getName();
		}

		private ClockTimerTask clockTimerTask;
		private Integer initialDelay;
		private Integer delay;
		private String timerTaskClassName;

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

		public String getTimerTaskClassName() {
			return timerTaskClassName;
		}

		public void setTimerTaskClassName(String timerTaskClassName) {
			this.timerTaskClassName = timerTaskClassName;
		}

		public ClockTimerTask getClockTimerTask() {
			return clockTimerTask;
		}

		public void setClockTimerTask(ClockTimerTask clockTimerTask) {
			this.clockTimerTask = clockTimerTask;
		}

	}

	public class ScheduleJob extends GsonPropertyObject {

		public <T extends ClockScheduleTask> ScheduleJob(Class<T> clz, String cron) {
			this.cron = cron;
			this.timerTaskClassName = clz.getName();
		}

		private String cron;
		private String timerTaskClassName;

		public String getTimerTaskClassName() {
			return timerTaskClassName;
		}

		public void setTimerTaskClassName(String timerTaskClassName) {
			this.timerTaskClassName = timerTaskClassName;
		}

		public String getCron() {
			return cron;
		}

		public void setCron(String cron) {
			this.cron = cron;
		}

	}

	public void destrory(ServletContextEvent servletContextEvent) {
		try {
			queues.stream().forEach(p -> {
				p.stop();
			});
			scheduledExecutorService.shutdownNow();
			EntityManagerContainerFactory.close();
			PCRegistry.deRegister(JpaObject.class.getClassLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
