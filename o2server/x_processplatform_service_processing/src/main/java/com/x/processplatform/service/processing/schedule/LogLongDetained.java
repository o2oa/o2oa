package com.x.processplatform.service.processing.schedule;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.core.entity.element.ActivityType;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class LogLongDetained extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogLongDetained.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			AtomicInteger workCount = new AtomicInteger();
			AtomicInteger taskCount = new AtomicInteger();
			AtomicInteger readCount = new AtomicInteger();
			Date workThreshold = DateUtils.addMinutes(new Date(),
					0 - Config.processPlatform().getLogLongDetained().getWorkThresholdMinutes());
			Date taskThreshold = DateUtils.addMinutes(new Date(),
					0 - Config.processPlatform().getLogLongDetained().getTaskThresholdMinutes());
			Date readThreshold = DateUtils.addMinutes(new Date(),
					0 - Config.processPlatform().getLogLongDetained().getReadThresholdMinutes());
			Wo wo = new Wo();
			wo.setWorks(works(workCount, workThreshold));
			wo.setTasks(tasks(taskCount, taskThreshold));
			wo.setReads(reads(readCount, readThreshold));
			java.nio.file.Path path = new File(Config.dir_logs(true), "longDetained.json").toPath();
			Files.writeString(path, XGsonBuilder.toJson(wo), StandardCharsets.UTF_8);
			LOGGER.info("记录长时间停滞工作{}个, 待办{}个, 待阅{}个, 耗时:{}.", workCount.intValue(), taskCount.intValue(),
					readCount.intValue(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<WoRead> reads(AtomicInteger readCount, Date readThreshold) throws Exception {
		List<Read> reads = new ArrayList<>();
		List<WoRead> wos = new ArrayList<>();
		String sequence = "";
		do {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				reads = this.listRead(emc, readThreshold, sequence);
			}
			if (!reads.isEmpty()) {
				sequence = reads.get(reads.size() - 1).getSequence();
				for (Read read : reads) {
					WoRead woRead = new WoRead();
					woRead.setId(read.getId());
					woRead.setJob(read.getJob());
					woRead.setStartTime(read.getStartTime());
					woRead.setTitle(read.getTitle());
					woRead.setActivity(read.getActivity());
					woRead.setActivityType(read.getActivityType());
					woRead.setActivityName(read.getActivityName());
					woRead.setIdentity(read.getIdentity());
					woRead.setUnit(read.getUnit());
					woRead.setPerson(read.getPerson());
					wos.add(woRead);
					readCount.incrementAndGet();
				}
			}
		} while (!reads.isEmpty());
		return wos;
	}

	private List<WoTask> tasks(AtomicInteger taskCount, Date taskThreshold) throws Exception {
		List<Task> tasks = new ArrayList<>();
		List<WoTask> wos = new ArrayList<>();
		String sequence = "";
		do {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				tasks = this.listTask(emc, taskThreshold, sequence);
			}
			if (!tasks.isEmpty()) {
				sequence = tasks.get(tasks.size() - 1).getSequence();
				for (Task task : tasks) {
					WoTask woTask = new WoTask();
					woTask.setId(task.getId());
					woTask.setJob(task.getJob());
					woTask.setStartTime(task.getStartTime());
					woTask.setTitle(task.getTitle());
					woTask.setActivity(task.getActivity());
					woTask.setActivityType(task.getActivityType());
					woTask.setActivityName(task.getActivityName());
					woTask.setIdentity(task.getIdentity());
					woTask.setUnit(task.getUnit());
					woTask.setPerson(task.getPerson());
					wos.add(woTask);
					taskCount.incrementAndGet();
				}
			}
		} while (!tasks.isEmpty());
		return wos;
	}

	private List<WoWork> works(AtomicInteger workCount, Date workThreshold) throws Exception {
		List<Work> works = new ArrayList<>();
		List<WoWork> wos = new ArrayList<>();
		String sequence = "";
		do {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				works = this.listWork(emc, workThreshold, sequence);
			}
			if (!works.isEmpty()) {
				sequence = works.get(works.size() - 1).getSequence();
				for (Work work : works) {
					WoWork woWork = new WoWork();
					woWork.setId(work.getId());
					woWork.setJob(work.getJob());
					woWork.setTitle(work.getTitle());
					woWork.setActivity(work.getActivity());
					woWork.setActivityName(work.getActivityName());
					woWork.setActivityType(work.getActivityType());
					woWork.setActivityArrivedTime(work.getActivityArrivedTime());
					wos.add(woWork);
					workCount.incrementAndGet();
				}
			}
		} while (!works.isEmpty());
		return wos;
	}

	private List<Work> listWork(EntityManagerContainer emc, Date threshold, String sequence) throws Exception {
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Work> root = cq.from(Work.class);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Path<String> idPath = root.get(Work_.id);
		Path<String> jobPath = root.get(Work_.job);
		Path<String> titlePath = root.get(Work_.title);
		Path<String> activityPath = root.get(Work_.activity);
		Path<String> activityNamePath = root.get(Work_.activityName);
		Path<ActivityType> activityTypePath = root.get(Work_.activityType);
		Path<Date> activityArrivedTimePath = root.get(Work_.activityArrivedTime);
		Predicate p = cb.lessThan(activityArrivedTimePath, threshold);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath, titlePath, activityPath, activityNamePath, activityTypePath,
				activityArrivedTimePath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Work> list = new ArrayList<>();
		for (Tuple o : os) {
			Work work = new Work();
			work.setSequence(o.get(sequencePath));
			work.setId(o.get(idPath));
			work.setJob(o.get(jobPath));
			work.setTitle(o.get(titlePath));
			work.setActivity(o.get(activityPath));
			work.setActivityName(o.get(activityNamePath));
			work.setActivityType(o.get(activityTypePath));
			work.setActivityArrivedTime(o.get(activityArrivedTimePath));
			list.add(work);
		}
		return list;
	}

	private List<Task> listTask(EntityManagerContainer emc, Date threshold, String sequence) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Path<String> idPath = root.get(Task_.id);
		Path<String> jobPath = root.get(Task_.job);
		Path<String> titlePath = root.get(Task_.title);
		Path<String> activityPath = root.get(Task_.activity);
		Path<String> activityNamePath = root.get(Task_.activityName);
		Path<ActivityType> activityTypePath = root.get(Task_.activityType);
		Path<String> identityPath = root.get(Task_.identity);
		Path<String> unitPath = root.get(Task_.unit);
		Path<String> personPath = root.get(Task_.person);
		Path<Date> startTimePath = root.get(Task_.startTime);
		Predicate p = cb.lessThan(startTimePath, threshold);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath, titlePath, startTimePath, activityPath, activityNamePath,
				activityTypePath, identityPath, unitPath, personPath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setSequence(o.get(sequencePath));
			task.setId(o.get(idPath));
			task.setJob(o.get(jobPath));
			task.setTitle(o.get(titlePath));
			task.setStartTime(o.get(startTimePath));
			task.setActivity(o.get(activityPath));
			task.setActivityName(o.get(activityNamePath));
			task.setActivityType(o.get(activityTypePath));
			task.setIdentity(o.get(identityPath));
			task.setUnit(o.get(unitPath));
			task.setPerson(o.get(personPath));
			list.add(task);
		}
		return list;
	}

	private List<Read> listRead(EntityManagerContainer emc, Date threshold, String sequence) throws Exception {
		EntityManager em = emc.get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Read> root = cq.from(Read.class);
		Path<String> sequencePath = root.get(JpaObject_.sequence);
		Path<String> idPath = root.get(Read_.id);
		Path<String> jobPath = root.get(Read_.job);
		Path<String> titlePath = root.get(Read_.title);
		Path<String> activityPath = root.get(Read_.activity);
		Path<String> activityNamePath = root.get(Read_.activityName);
		Path<ActivityType> activityTypePath = root.get(Read_.activityType);
		Path<String> identityPath = root.get(Read_.identity);
		Path<String> unitPath = root.get(Read_.unit);
		Path<String> personPath = root.get(Read_.person);
		Path<Date> startTimePath = root.get(Read_.startTime);
		Predicate p = cb.lessThan(startTimePath, threshold);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequencePath, sequence));
		}
		cq.multiselect(idPath, jobPath, sequencePath, titlePath, startTimePath, activityPath, activityNamePath,
				activityTypePath, identityPath, unitPath, personPath).where(p).orderBy(cb.asc(sequencePath));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Read> list = new ArrayList<>();
		for (Tuple o : os) {
			Read read = new Read();
			read.setId(o.get(idPath));
			read.setJob(o.get(jobPath));
			read.setSequence(o.get(sequencePath));
			read.setTitle(o.get(titlePath));
			read.setStartTime(o.get(startTimePath));
			read.setActivity(o.get(activityPath));
			read.setActivityName(o.get(activityNamePath));
			read.setActivityType(o.get(activityTypePath));
			read.setIdentity(o.get(identityPath));
			read.setUnit(o.get(unitPath));
			read.setPerson(o.get(personPath));
			list.add(read);
		}
		return list;
	}

	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 1692478481957435007L;

		private List<WoWork> works;
		private List<WoTask> tasks;
		private List<WoRead> reads;

		public List<WoWork> getWorks() {
			return works;
		}

		public void setWorks(List<WoWork> works) {
			this.works = works;
		}

		public List<WoTask> getTasks() {
			return tasks;
		}

		public void setTasks(List<WoTask> tasks) {
			this.tasks = tasks;
		}

		public List<WoRead> getReads() {
			return reads;
		}

		public void setReads(List<WoRead> reads) {
			this.reads = reads;
		}

	}

	public class WoWork extends GsonPropertyObject {

		private static final long serialVersionUID = 4419028117657301978L;

		private String id;
		private String job;
		private String title;
		private String activityName;
		private String activity;
		private ActivityType activityType;
		private Date activityArrivedTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public Date getActivityArrivedTime() {
			return activityArrivedTime;
		}

		public void setActivityArrivedTime(Date activityArrivedTime) {
			this.activityArrivedTime = activityArrivedTime;
		}

	}

	public class WoTask extends GsonPropertyObject {

		private static final long serialVersionUID = 813395994927179190L;

		private String id;
		private String job;
		private String title;
		private String activityName;
		private String activity;
		private ActivityType activityType;
		private Date startTime;
		private String person;
		private String identity;
		private String unit;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

	}

	public class WoRead extends GsonPropertyObject {

		private static final long serialVersionUID = 8668670439726236462L;

		private String id;
		private String job;
		private String title;
		private String activityName;
		private String activity;
		private ActivityType activityType;
		private Date startTime;
		private String person;
		private String identity;
		private String unit;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getJob() {
			return job;
		}

		public void setJob(String job) {
			this.job = job;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getActivityName() {
			return activityName;
		}

		public void setActivityName(String activityName) {
			this.activityName = activityName;
		}

		public String getActivity() {
			return activity;
		}

		public void setActivity(String activity) {
			this.activity = activity;
		}

		public ActivityType getActivityType() {
			return activityType;
		}

		public void setActivityType(ActivityType activityType) {
			this.activityType = activityType;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}
}