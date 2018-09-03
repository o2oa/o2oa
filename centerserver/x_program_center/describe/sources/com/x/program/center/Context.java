package com.x.program.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.enhance.PCRegistry;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.jaxrs.WrapClearCacheRequest;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.schedule.JobReportListener;
import com.x.base.core.project.schedule.SchedulerFactoryProperties;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SslTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.program.center.core.entity.Schedule;
import com.x.program.center.core.entity.ScheduleLocal;

public class Context extends AbstractContext {

	private static Logger logger = LoggerFactory.getLogger(Context.class);

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

	/* 清除缓存指定队列 */
	private AbstractQueue<WrapClearCacheRequest> clearCacheRequestQueue;

	public AbstractQueue<WrapClearCacheRequest> clearCacheRequestQueue() {
		return this.clearCacheRequestQueue;
	}

	/* quartz 调度器 */
	private Scheduler scheduler;

	/* 队列 */
	private List<AbstractQueue<?>> queues;

	private Context() throws Exception {
		SslTools.ignoreSsl();
		// SLF4JBridgeHandler.removeHandlersForRootLogger();
		// SLF4JBridgeHandler.install();
		// Logger.getLogger(QuartzScheduler.class.getName()).setLevel(Level.WARNING);
		this.applications = new Applications();
		this.token = UUID.randomUUID().toString();
		this.queues = new ArrayList<AbstractQueue<?>>();
		this.scheduler = new StdSchedulerFactory(SchedulerFactoryProperties.concrete()).getScheduler();
		this.scheduler.getListenerManager().addJobListener(new JobReportListener(), EverythingMatcher.allJobs());
		this.scheduler.start();

	}

	public static Context concrete(ServletContextEvent servletContextEvent) throws Exception {
		ServletContext servletContext = servletContextEvent.getServletContext();
		Context context = new Context();
		context.path = servletContext.getRealPath("");
		context.servletContext = servletContext;
		context.servletContextName = servletContext.getServletContextName();
		context.clazz = Class.forName("com.x.base.core.project." + context.servletContextName);
		context.initDatas();
		context.initStorages();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			context.cleanupSchedule(emc);
			context.cleanupScheduleLocal(emc);
			context.checkDefaultRole(emc);
		}
		servletContext.setAttribute(context.getClass().getName(), context);
		return context;
	}

	public <T extends Job> void scheduleLocal(Class<T> cls, String cron) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public <T extends Job> void scheduleLocal(Class<T> cls, Trigger existTrigger) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").withSchedule(existTrigger.getScheduleBuilder()).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public <T extends Job> void scheduleLocal(Class<T> cls, Integer delay, Integer interval) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
				.withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
				.withDescription("scheduleLocal").startAt(DateBuilder.futureDate(delay, IntervalUnit.SECOND))
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(interval).repeatForever())
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public <T extends Job> void scheduleLocal(Class<T> cls) throws Exception {
		/* 需要单独生成一个独立任务,保证group和预约的任务不重复 */
		String group = StringTools.uniqueToken();
		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), group).withDescription(Config.node())
				.build();
		/* 经过测试0代表不重复,进运行一次 */
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), group)
				.withDescription("scheduleLocal")
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(0))
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public void startQueue(AbstractQueue<?> queue) {
		queues.add(queue);
		queue.start();
	}

	private void initDatas() throws Exception {
		@SuppressWarnings("unchecked")
		List<String> containerEntities = (List<String>) FieldUtils.readStaticField(clazz, "containerEntities");
		if (ListTools.isNotEmpty(containerEntities)) {
			logger.print("{} loading datas, entity size:{}.", this.clazz.getName(), containerEntities.size());
			EntityManagerContainerFactory.init(path, Config.dataMappings());
		}
	}

	private void initStorages() throws Exception {
		@SuppressWarnings("unchecked")
		List<StorageType> usedStorageTypes = (List<StorageType>) FieldUtils.readStaticField(clazz, "usedStorageTypes");
		if (ListTools.isNotEmpty(usedStorageTypes)) {
			logger.print("{} loading storages, type size:{}.", this.clazz.getName(), usedStorageTypes.size());
			this.storageMappings = Config.storageMappings();
		}
	}

	private void cleanupSchedule(EntityManagerContainer emc) throws Exception {
		List<Schedule> list = emc.listAll(Schedule.class);
		if (!list.isEmpty()) {
			emc.beginTransaction(Schedule.class);
			list.stream().forEach(o -> {
				try {
					emc.remove(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			emc.commit();
		}
	}

	private void cleanupScheduleLocal(EntityManagerContainer emc) throws Exception {
		List<ScheduleLocal> list = emc.listAll(ScheduleLocal.class);
		if (!list.isEmpty()) {
			emc.beginTransaction(ScheduleLocal.class);
			list.stream().forEach(o -> {
				try {
					emc.remove(o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			emc.commit();
		}
	}

	private void checkDefaultRole(EntityManagerContainer emc) throws Exception {
		List<String> roles = ListTools.toList(OrganizationDefinition.Manager, OrganizationDefinition.AttendanceManager,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.PersonManager,
				OrganizationDefinition.GroupManager, OrganizationDefinition.UnitManager,
				OrganizationDefinition.RoleManager, OrganizationDefinition.ProcessPlatformManager,
				OrganizationDefinition.ProcessPlatformCreator, OrganizationDefinition.MeetingManager,
				OrganizationDefinition.PortalManager, OrganizationDefinition.BBSManager,
				OrganizationDefinition.CMSManager, OrganizationDefinition.OKRManager, OrganizationDefinition.CRMManager,
				OrganizationDefinition.QueryManager);
		roles = roles.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
		for (String str : roles) {
			EntityManager em = emc.get(Role.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Role> root = cq.from(Role.class);
			Predicate p = cb.equal(root.get(Role_.name), str);
			cq.select(root.get(Role_.id)).where(p);
			List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
			if (list.isEmpty()) {
				Role o = new Role();
				o.setName(str);
				o.setUnique(str + OrganizationDefinition.RoleDefinitionSuffix);
				emc.beginTransaction(Role.class);
				emc.persist(o, CheckPersistType.all);
				emc.commit();
			}
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

}