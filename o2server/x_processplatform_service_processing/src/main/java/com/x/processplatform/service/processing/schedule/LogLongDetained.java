package com.x.processplatform.service.processing.schedule;

import java.io.File;
import java.io.FileWriter;
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
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Read_;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.Task_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;

import fr.opensagres.poi.xwpf.converter.core.utils.StringUtils;

public class LogLongDetained extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(LogLongDetained.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			TimeStamp stamp = new TimeStamp();
			String sequence = null;
			AtomicInteger workCount = new AtomicInteger();
			AtomicInteger taskCount = new AtomicInteger();
			AtomicInteger readCount = new AtomicInteger();
			Date workThreshold = DateUtils.addMinutes(new Date(),
					0 - Config.processPlatform().getLogLongDetained().getWorkThresholdMinutes());
			Date taskThreshold = DateUtils.addMinutes(new Date(),
					0 - Config.processPlatform().getLogLongDetained().getTaskThresholdMinutes());
			Date readThreshold = DateUtils.addMinutes(new Date(),
					0 - Config.processPlatform().getLogLongDetained().getReadThresholdMinutes());
			File file = new File(Config.dir_logs(true),
					"longDetained_" + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".txt");
			try (FileWriter writer = new FileWriter(file, true)) {
				List<Work> works = new ArrayList<>();
				List<Read> reads = new ArrayList<>();
				List<Task> tasks = new ArrayList<>();
				do {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						works = this.listWork(emc, workThreshold, sequence);
					}
					if (!works.isEmpty()) {
						sequence = works.get(works.size() - 1).getSequence();
						for (Work work : works) {
							writer.append("work id:").append(work.getId()).append(", job:").append(work.getJob())
									.append(", startTime:")
									.append(DateTools.format(work.getStartTime(), DateTools.format_yyyyMMddHHmmss))
									.append(", title:").append(work.getTitle()).append(".")
									.append(System.lineSeparator());
							workCount.incrementAndGet();
						}
					}
				} while (!works.isEmpty());
				do {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						tasks = this.listTask(emc, taskThreshold, sequence);
					}
					if (!tasks.isEmpty()) {
						sequence = tasks.get(tasks.size() - 1).getSequence();
						for (Task task : tasks) {
							writer.append("task id:").append(task.getId()).append(", job:").append(task.getJob())
									.append(", startTime:")
									.append(DateTools.format(task.getStartTime(), DateTools.format_yyyyMMddHHmmss))
									.append(", title:").append(task.getTitle()).append(".")
									.append(System.lineSeparator());
							taskCount.incrementAndGet();
						}
					}
				} while (!tasks.isEmpty());
				do {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						reads = this.listRead(emc, readThreshold, sequence);
					}
					if (!reads.isEmpty()) {
						sequence = reads.get(reads.size() - 1).getSequence();
						for (Read read : reads) {
							writer.append("read id:").append(read.getId()).append(", job:").append(read.getJob())
									.append(", startTime:")
									.append(DateTools.format(read.getStartTime(), DateTools.format_yyyyMMddHHmmss))
									.append(", title:").append(read.getTitle()).append(".")
									.append(System.lineSeparator());
							readCount.incrementAndGet();
						}
					}
				} while (!reads.isEmpty());
			}
			logger.print("记录长时间停滞工作{}个,待办{}个,待阅{}个, 耗时:{}.", workCount.intValue(), taskCount.intValue(),
					readCount.intValue(), stamp.consumingMilliseconds());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	private List<Work> listWork(EntityManagerContainer emc, Date threshold, String sequence) throws Exception {
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Work> root = cq.from(Work.class);
		Path<String> id_path = root.get(Work_.id);
		Path<String> job_path = root.get(Work_.job);
		Path<String> sequence_path = root.get(Work_.sequence);
		Path<String> title_path = root.get(Work_.title);
		Path<Date> activityArrivedTime_path = root.get(Work_.activityArrivedTime);
		Predicate p = cb.lessThan(activityArrivedTime_path, threshold);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequence_path, sequence));
		}
		cq.multiselect(id_path, job_path, sequence_path, title_path, activityArrivedTime_path).where(p)
				.orderBy(cb.asc(sequence_path));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Work> list = new ArrayList<>();
		for (Tuple o : os) {
			Work work = new Work();
			work.setId(o.get(id_path));
			work.setJob(o.get(job_path));
			work.setSequence(o.get(sequence_path));
			work.setTitle(o.get(title_path));
			work.setStartTime(o.get(activityArrivedTime_path));
			list.add(work);
		}
		return list;
	}

	private List<Task> listTask(EntityManagerContainer emc, Date threshold, String sequence) throws Exception {
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Path<String> id_path = root.get(Task_.id);
		Path<String> job_path = root.get(Task_.job);
		Path<String> sequence_path = root.get(Task_.sequence);
		Path<String> title_path = root.get(Task_.title);
		Path<Date> startTime_path = root.get(Task_.startTime);
		Predicate p = cb.lessThan(startTime_path, threshold);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequence_path, sequence));
		}
		cq.multiselect(id_path, job_path, sequence_path, title_path, startTime_path).where(p)
				.orderBy(cb.asc(sequence_path));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setId(o.get(id_path));
			task.setJob(o.get(job_path));
			task.setSequence(o.get(sequence_path));
			task.setTitle(o.get(title_path));
			task.setStartTime(o.get(startTime_path));
			list.add(task);
		}
		return list;
	}

	private List<Read> listRead(EntityManagerContainer emc, Date threshold, String sequence) throws Exception {
		EntityManager em = emc.get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Read> root = cq.from(Read.class);
		Path<String> id_path = root.get(Read_.id);
		Path<String> job_path = root.get(Read_.job);
		Path<String> sequence_path = root.get(Read_.sequence);
		Path<String> title_path = root.get(Read_.title);
		Path<Date> startTime_path = root.get(Read_.startTime);
		Predicate p = cb.lessThan(startTime_path, threshold);
		if (StringUtils.isNotEmpty(sequence)) {
			p = cb.and(p, cb.greaterThan(sequence_path, sequence));
		}
		cq.multiselect(id_path, job_path, sequence_path, title_path, startTime_path).where(p)
				.orderBy(cb.asc(sequence_path));
		List<Tuple> os = em.createQuery(cq).setMaxResults(200).getResultList();
		List<Read> list = new ArrayList<>();
		for (Tuple o : os) {
			Read read = new Read();
			read.setId(o.get(id_path));
			read.setJob(o.get(job_path));
			read.setSequence(o.get(sequence_path));
			read.setTitle(o.get(title_path));
			read.setStartTime(o.get(startTime_path));
			list.add(read);
		}
		return list;
	}
}