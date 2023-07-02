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

import org.apache.openjpa.enhance.PCRegistry;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMappings;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.schedule.JobReportListener;
import com.x.base.core.project.schedule.SchedulerFactoryProperties;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SslTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

public class Context extends AbstractContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

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

    /* name */
    private volatile String name;

    public String name() {
        return this.name;
    }

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

    private Boolean sslEnable;

    public Boolean sslEnable() {
        return this.sslEnable;
    }

    /* quartz 调度器 */
    private Scheduler scheduler;

    /* 队列 */
    private List<AbstractQueue<?>> queues;

    private Context() throws Exception {
        SslTools.ignoreSsl();
        this.applications = new Applications();
        this.token = UUID.randomUUID().toString();
        this.queues = new ArrayList<>();
    }

    public static Context concrete(ServletContextEvent servletContextEvent) throws Exception {
        ServletContext servletContext = servletContextEvent.getServletContext();
        Context context = new Context();
        context.contextPath = servletContext.getContextPath();
        context.clazz = Thread.currentThread().getContextClassLoader()
                .loadClass(servletContext.getInitParameter(INITPARAMETER_PORJECT));
        context.module = context.clazz.getAnnotation(Module.class);
        context.name = context.module.name();
        context.path = servletContext.getRealPath("");
        context.servletContext = servletContext;
        context.servletContextName = servletContext.getServletContextName();
        context.clazz = Thread.currentThread().getContextClassLoader()
                .loadClass(servletContextEvent.getServletContext().getInitParameter(INITPARAMETER_PORJECT));
        context.initDatas();
        // context.threadFactory = new ThreadFactory(context);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            context.checkDefaultRole(emc);
        }
        servletContext.setAttribute(AbstractContext.class.getName(), context);
        SchedulerFactoryProperties schedulerFactoryProperties = SchedulerFactoryProperties.concrete();
        schedulerFactoryProperties.setProperty("org.quartz.scheduler.instanceName",
                "ContextQuartzScheduler-" + context.clazz().getSimpleName());
        context.scheduler = new StdSchedulerFactory(schedulerFactoryProperties).getScheduler();
        context.scheduler.getListenerManager().addJobListener(new JobReportListener(), EverythingMatcher.allJobs());
        context.scheduler.start();
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
        LOGGER.print("{} loading datas, entity size:{}.", this.clazz.getName(), this.module.containerEntities().length);
        EntityManagerContainerFactory.init(path, ListTools.toList(this.module.containerEntities()));
    }

    private void checkDefaultRole(EntityManagerContainer emc) throws Exception {
        List<String> roles = ListTools.toList(OrganizationDefinition.Manager, OrganizationDefinition.AttendanceManager,
                OrganizationDefinition.OrganizationManager, OrganizationDefinition.PersonManager,
                OrganizationDefinition.GroupManager, OrganizationDefinition.UnitManager,
                OrganizationDefinition.RoleManager, OrganizationDefinition.ProcessPlatformManager,
                OrganizationDefinition.ProcessPlatformCreator, OrganizationDefinition.MeetingManager,
                OrganizationDefinition.PortalManager, OrganizationDefinition.PortalCreator,
                OrganizationDefinition.BBSManager, OrganizationDefinition.TeamWorkManager,
                OrganizationDefinition.CMSManager, OrganizationDefinition.OKRManager, OrganizationDefinition.CRMManager,
                OrganizationDefinition.CMSCreator, OrganizationDefinition.QueryManager,
                OrganizationDefinition.QueryCreator, OrganizationDefinition.MessageManager,
                OrganizationDefinition.SearchPrivilege, OrganizationDefinition.HotPictureManager,
                OrganizationDefinition.FileManager, OrganizationDefinition.ServiceManager);
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
                o.setDescription(getDescriptionWithName(str));
                emc.beginTransaction(Role.class);
                emc.persist(o, CheckPersistType.all);
                emc.commit();
            }
        }
    }

    /**
     * , OrganizationDefinition., OrganizationDefinition., OrganizationDefinition.,
     * OrganizationDefinition., OrganizationDefinition., OrganizationDefinition.,
     * OrganizationDefinition., OrganizationDefinition., OrganizationDefinition.,
     * OrganizationDefinition., OrganizationDefinition., OrganizationDefinition.,
     * OrganizationDefinition., ., OrganizationDefinition., OrganizationDefinition.,
     * OrganizationDefinition.
     *
     * @param str
     * @return
     */
    private String getDescriptionWithName(String str) {
        if (OrganizationDefinition.Manager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.Manager_description;
        } else if (OrganizationDefinition.AttendanceManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.AttendanceManager_description;
        } else if (OrganizationDefinition.OrganizationManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.OrganizationManager_description;
        } else if (OrganizationDefinition.PersonManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.PersonManager_description;
        } else if (OrganizationDefinition.GroupManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.GroupManager_description;
        } else if (OrganizationDefinition.UnitManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.UnitManager_description;
        } else if (OrganizationDefinition.RoleManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.RoleManager_description;
        } else if (OrganizationDefinition.ProcessPlatformManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.ProcessPlatformManager_description;
        } else if (OrganizationDefinition.ProcessPlatformCreator.equalsIgnoreCase(str)) {
            return OrganizationDefinition.ProcessPlatformCreator_description;
        } else if (OrganizationDefinition.MeetingManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.MeetingManager_description;
        } else if (OrganizationDefinition.MeetingViewer.equalsIgnoreCase(str)) {
            return OrganizationDefinition.MeetingViewer_description;
        } else if (OrganizationDefinition.PortalManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.PortalManager_description;
        } else if (OrganizationDefinition.PortalCreator.equalsIgnoreCase(str)) {
            return OrganizationDefinition.PortalCreator_description;
        } else if (OrganizationDefinition.BBSManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.BBSManager_description;
        } else if (OrganizationDefinition.CMSManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.CMSManager_description;
        } else if (OrganizationDefinition.CMSCreator.equalsIgnoreCase(str)) {
            return OrganizationDefinition.CMSCreator_description;
        } else if (OrganizationDefinition.OKRManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.OKRManager_description;
        } else if (OrganizationDefinition.CRMManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.CRMManager_description;
        } else if (OrganizationDefinition.TeamWorkManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.TeamWorkManager_description;
        } else if (OrganizationDefinition.QueryManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.QueryManager_description;
        } else if (OrganizationDefinition.QueryCreator.equalsIgnoreCase(str)) {
            return OrganizationDefinition.QueryCreator_description;
        } else if (OrganizationDefinition.MessageManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.MessageManager_description;
        } else if (OrganizationDefinition.SearchPrivilege.equalsIgnoreCase(str)) {
            return OrganizationDefinition.SearchPrivilege_description;
        } else if (OrganizationDefinition.FileManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.FileManager_description;
        } else if (OrganizationDefinition.ServiceManager.equalsIgnoreCase(str)) {
            return OrganizationDefinition.ServiceManager_description;
        }
        return "";
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

    @Override
    @Deprecated
    // 重来没用调用过的方法,目标是全部方法全部移动到AbstractContext中
//	public <T extends AbstractJob> void fireScheduleOnLocal(Class<T> cls, Integer delay) throws Exception {
//		/* 需要单独生成一个独立任务,保证group和预约的任务不重复 */
//		JobDataMap jobDataMap = new JobDataMap();
//		jobDataMap.put("context", this);
//		JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(cls.getName(), clazz.getName())
//				.usingJobData(jobDataMap).withDescription(Config.node()).build();
//		/* 经过测试0代表不重复,仅运行一次 */
//		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(cls.getName(), clazz.getName())
//				.withDescription("schedule").startAt(DateBuilder.futureDate(delay, IntervalUnit.SECOND))
//				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(0))
//				.build();
//		scheduler.scheduleJob(jobDetail, trigger);
//	}

    public <T extends AbstractJob> void fireScheduleOnLocal(Class<T> cls, Integer delay) throws Exception {
        // 需要单独生成一个独立任务,保证group和预约的任务不重复
        // https://stackoverflow.com/questions/34176482/unable-to-store-job-because-one-already-exists-with-this-identification
        String uniqueGroup = StringTools.uniqueToken();
        JobKey jobKey = JobKey.jobKey(cls.getName(), uniqueGroup);
        TriggerKey triggerKey = TriggerKey.triggerKey(cls.getName(), uniqueGroup);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("context", this);
        JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobKey).usingJobData(jobDataMap)
                .withDescription(Config.node()).build();
        // 经过测试0代表不重复,仅运行一次
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withDescription("schedule")
                .startAt(DateBuilder.futureDate(delay, IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(0))
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
