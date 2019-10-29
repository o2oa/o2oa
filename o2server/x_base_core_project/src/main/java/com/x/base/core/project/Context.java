package com.x.base.core.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.openjpa.enhance.PCRegistry;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.schedule.JobReportListener;
import com.x.base.core.project.schedule.ScheduleLocalRequest;
import com.x.base.core.project.schedule.ScheduleRequest;
import com.x.base.core.project.schedule.SchedulerFactoryProperties;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SslTools;
import com.x.base.core.project.tools.StringTools;

public class Context extends AbstractContext {

	private static Logger logger = LoggerFactory.getLogger(Context.class);

	/* 从servletContext中抽取context */
	public static Context fromServletContext(ServletContext servletContext) throws Exception {
		Object o = servletContext.getAttribute(Context.class.getName());
		if (null == o) {
			throw new Exception("can not get context form servletContext.");
		} else {
			return (Context) o;
		}
	}

	/* 从servletRequest中抽取context */
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

	/* 随机令牌 */
	private volatile String token;

	public String token() {
		return this.token;
	}

	/* contextPath */
	private volatile String contextPath;

	public String contextPath() {
		return this.contextPath;
	}

	/* title */
	private volatile String name;

	public String name() {
		return this.name;
	}

//	/* Storage资源 */

	public StorageMappings storageMappings() throws Exception {
		return Config.storageMappings();
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

	/* 应用的权重 */
	private volatile Integer scheduleWeight;

	public Integer scheduleWeight() {
		return this.scheduleWeight;
	}

	private Boolean sslEnable;

	public Boolean sslEnable() {
		return this.sslEnable;
	}

	/* quartz 调度器 */
	public Scheduler scheduler;

	/* 本地任务说明 */
	private List<ScheduleLocalRequest> scheduleLocalRequestList = new ArrayList<>();

	/* 集群任务说明 */
	private List<ScheduleRequest> scheduleRequestList = new ArrayList<>();

	private AbstractQueue<WrapClearCacheRequest> clearCacheRequestQueue;

	public AbstractQueue<WrapClearCacheRequest> clearCacheRequestQueue() {
		return this.clearCacheRequestQueue;
	}

	private volatile Date applicationsTimestamp = null;

	public Applications applications() throws Exception {
		if (null != Config.resource_node_applicationsTimestamp()) {
			if (null == this.applicationsTimestamp
					|| (this.applicationsTimestamp.before(Config.resource_node_applicationsTimestamp()))) {
				synchronized (this) {
					this.applications = XGsonBuilder.instance().fromJson(Config.resource_node_applications(),
							Applications.class);
					this.applicationsTimestamp = Config.resource_node_applicationsTimestamp();
				}
			}
		}
		return this.applications;
	}

	/* 队列 */
	private List<AbstractQueue<?>> queues;

	private Context() throws Exception {
		this.token = UUID.randomUUID().toString();
		this.applications = new Applications();
		this.queues = new ArrayList<AbstractQueue<?>>();
		this.scheduler = new StdSchedulerFactory(SchedulerFactoryProperties.concrete()).getScheduler();
		this.scheduler.getListenerManager().addJobListener(new JobReportListener(), EverythingMatcher.allJobs());
		this.scheduler.start();
	}

	public static Context concrete(ServletContextEvent servletContextEvent) throws Exception {
		/* 强制忽略ssl服务器认证 */
		SslTools.ignoreSsl();
		ServletContext servletContext = servletContextEvent.getServletContext();
		Context context = new Context();
		context.contextPath = servletContext.getContextPath();
		context.clazz = Class.forName(servletContext.getInitParameter(INITPARAMETER_PORJECT));
		context.module = context.clazz.getAnnotation(Module.class);
		context.name = getName(context.clazz);
		context.path = servletContext.getRealPath("");
		context.servletContext = servletContext;
		context.servletContextName = servletContext.getServletContextName();
		context.weight = Config.currentNode().getApplication().weight(context.clazz);
		context.scheduleWeight = Config.currentNode().getApplication().scheduleWeight(context.clazz);
		context.sslEnable = Config.currentNode().getApplication().getSslEnable();
		context.initDatas();
		servletContext.setAttribute(context.getClass().getName(), context);
		context.initialized = true;
		/* 20190927新注册机制 */
		// registApplication(context);
		return context;
	}

	/* 20190927新注册机制 */
	public void regist() throws Exception {
		Application application = new Application();
		application.setClassName(this.clazz().getName());
		application.setName(this.name());
		application.setNode(Config.node());
		application.setContextPath(this.contextPath());
		application.setPort(Config.currentNode().getApplication().getPort());
		application.setSslEnable(this.sslEnable());
		application.setProxyHost(Config.currentNode().getApplication().getProxyHost());
		application.setProxyPort(Config.currentNode().getApplication().getProxyPort());
		application.setWeight(this.weight());
		application.setScheduleWeight(this.scheduleWeight());
		application.setScheduleLocalRequestList(this.scheduleLocalRequestList);
		application.setScheduleRequestList(this.scheduleRequestList);
		JsonElement jsonElement = XGsonBuilder.instance().toJsonTree(application);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		jsonObject.addProperty("type", "registApplication");
		Config.resource_node_eventQueue().put(jsonObject);
	}

	public <T extends AbstractJob> void scheduleLocal(Class<T> cls, String cron) throws Exception {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("context", this);
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.usingJobData(jobDataMap).withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
		scheduler.scheduleJob(jobDetail, trigger);
		this.scheduleLocalRequestList.add(new ScheduleLocalRequest(jobDetail, cron, null, null));
	}

	public <T extends AbstractJob> void scheduleLocal(Class<T> cls, Trigger existTrigger) throws Exception {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("context", this);
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.usingJobData(jobDataMap).withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").withSchedule(existTrigger.getScheduleBuilder()).build();
		scheduler.scheduleJob(jobDetail, trigger);
		this.scheduleLocalRequestList.add(new ScheduleLocalRequest(jobDetail, null, null, null));
	}

	public <T extends AbstractJob> void scheduleLocal(Class<T> cls, Integer delay, Integer interval) throws Exception {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("context", this);
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.usingJobData(jobDataMap).withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").startAt(DateBuilder.futureDate(delay, IntervalUnit.SECOND))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
		this.scheduleLocalRequestList.add(new ScheduleLocalRequest(jobDetail, null, delay, interval));
	}

	/*
	 * 接受Center调度的schedule在本地运行,和scheduleOnLocal没有本质区别,仅仅是个withDescription(
	 * "schedule")不一样,在log中记录这个值.
	 */
	public <T extends AbstractJob> void fireScheduleOnLocal(Class<T> cls, Integer delay) throws Exception {
		/* 需要单独生成一个独立任务,保证group和预约的任务不重复 */
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("context", this);
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.usingJobData(jobDataMap).withDescription(Config.node()).build();
		/* 经过测试0代表不重复,仅运行一次 */
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("schedule").startAt(DateBuilder.futureDate(delay, IntervalUnit.SECOND))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(0))
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public <T extends AbstractJob> void scheduleLocal(Class<T> cls, Integer delay) throws Exception {
		/* 需要单独生成一个独立任务,保证group和预约的任务不重复 */
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("context", this);
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.usingJobData(jobDataMap).withDescription(Config.node()).build();
		/* 经过测试0代表不重复,仅运行一次 */
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").startAt(DateBuilder.futureDate(delay, IntervalUnit.SECOND))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(0))
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	/* 统一排程任务需要延时90秒,等待instrument启动,每次间隔不能少于5分钟 */
	public <T extends AbstractJob> void schedule(Class<T> clz, String cron) throws Exception {
		this.scheduleRequestList.add(new ScheduleRequest(clz, Config.node(), cron));
	}

	public void startQueue(AbstractQueue<?> queue) {
		queues.add(queue);
		queue.start();
	}

	private void initDatas() throws Exception {
		if (ArrayUtils.isNotEmpty(this.module.containerEntities())) {
			logger.print("{} loading datas, entity size:{}.", this.clazz.getName(),
					this.module.containerEntities().length);
			EntityManagerContainerFactory.init(path, ListTools.toList(this.module.containerEntities()));
		}
	}

	public void destrory(ServletContextEvent servletContextEvent) {
		try {
			queues.stream().forEach(p -> {
				p.stop();
			});
			this.scheduler.shutdown();
			EntityManagerContainerFactory.close();
			PCRegistry.deRegister(JpaObject.class.getClassLoader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<ScheduleLocalRequest> getScheduleLocalRequestList() {
		return scheduleLocalRequestList;
	}

	public List<ScheduleRequest> getScheduleRequestList() {
		return scheduleRequestList;
	}

}
