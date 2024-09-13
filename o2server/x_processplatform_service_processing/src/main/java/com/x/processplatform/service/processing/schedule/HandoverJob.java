package com.x.processplatform.service.processing.schedule;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.Applications;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.core.entity.content.*;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * 权限交接任务处理
 * @author sword
 */
public class HandoverJob extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandoverJob.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		LOGGER.info("开始处理权限交接任务.");
		try {
			List<String> list = list();
			for (String id : list){
				dear(id);
			}
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
		LOGGER.info("完成处理权限交接任务.");
	}

	private void dear(String id){
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Set<String> jobSet = new HashSet<>();
			Business business = new Business(emc);
			Handover handover = emc.find(id, Handover.class);
			if(Handover.TYPE_REPLACE.equals(handover.getType())){
				toReplace(business, handover, jobSet);
			}else{
				toEmpower(business, handover, jobSet);
			}
			emc.beginTransaction(Handover.class);
			handover.setHandoverJobList(new ArrayList<>(jobSet));
			handover.setStatus(HandoverStatusEnum.PROCESSED.getValue());
			emc.commit();
			LOGGER.info("用户{}权限交接给{}, 交接文档数:{}, 耗时:{}.",handover::getPerson, handover::getTargetPerson, jobSet::size, stamp::consumingMilliseconds);
		}catch (Exception e){
			LOGGER.error(e);
		}
	}

	private void toReplace(Business business, Handover handover, Set<String> jobSet) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		Handover wi = new Handover();
		wi.setPerson(handover.getPerson());
		wi.setTargetIdentity(handover.getTargetIdentity());
		List<Task> taskList = listTask(business, handover);
		String path = "task";
		for (Task task : taskList){
			replaceTaskOrRead(task.getId(), task.getJob(), path, wi, jobSet);
		}
		List<Read> readList = listRead(business, handover);
		path = "read";
		for (Read read : readList){
			replaceTaskOrRead(read.getId(), read.getJob(), path, wi, jobSet);
		}
		List<String> list = listWork(business, handover);
		for (String id : list){
			Work work = emc.find(id, Work.class);
			emc.beginTransaction(Work.class);
			work.setCreatorPerson(handover.getTargetPerson());
			work.setCreatorIdentity(handover.getTargetIdentity());
			emc.commit();
			jobSet.add(work.getJob());
		}
		list = listWorkCompleted(business, handover);
		for (String id : list){
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			emc.beginTransaction(WorkCompleted.class);
			workCompleted.setCreatorPerson(handover.getTargetPerson());
			workCompleted.setCreatorIdentity(handover.getTargetIdentity());
			emc.commit();
			jobSet.add(workCompleted.getJob());
		}
		list = listTaskCompleted(business, handover);
		for (String id : list){
			TaskCompleted taskCompleted = emc.find(id, TaskCompleted.class);
			emc.beginTransaction(TaskCompleted.class);
			taskCompleted.setPerson(handover.getTargetPerson());
			taskCompleted.setIdentity(handover.getTargetIdentity());
			emc.commit();
			jobSet.add(taskCompleted.getJob());
		}
		list = listReadCompleted(business, handover);
		for (String id : list){
			ReadCompleted readCompleted = emc.find(id, ReadCompleted.class);
			emc.beginTransaction(ReadCompleted.class);
			readCompleted.setPerson(handover.getTargetPerson());
			readCompleted.setIdentity(handover.getTargetIdentity());
			emc.commit();
			jobSet.add(readCompleted.getJob());
		}
		list = listReview(business, handover);
		for (String id : list){
			Review review = emc.find(id, Review.class);
			Review targetReview = getReview(business, review.getJob(), handover.getTargetPerson());
			emc.beginTransaction(Review.class);
			if(targetReview == null){
				if(review.getPerson().equals(review.getCreatorPerson())){
					review.setCreatorPerson(handover.getTargetPerson());
				}
				review.setPerson(handover.getTargetPerson());
				jobSet.add(review.getJob());
			}else{
				if(review.getPerson().equals(review.getCreatorPerson())){
					targetReview.setCreatorPerson(handover.getTargetPerson());
				}
				emc.remove(review);
			}
			emc.commit();
			jobSet.add(review.getJob());
		}
		list = listDraft(business, handover);
		for (String id : list){
			Draft draft = emc.find(id, Draft.class);
			emc.beginTransaction(Draft.class);
			draft.setPerson(handover.getTargetPerson());
			draft.setIdentity(handover.getTargetIdentity());
			emc.commit();
			jobSet.add(draft.getId());
		}
	}

	private void toEmpower(Business business, Handover handover, Set<String> jobSet) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		List<String> reviewList = listReview(business, handover);
		for (String id : reviewList){
			Review review = emc.find(id, Review.class);
			if(!hasReview(business, review.getJob(), handover.getTargetPerson())){
				Review targetReview = new Review();
				review.copyTo(targetReview, JpaObject.FieldsUnmodify);
				targetReview.setPerson(handover.getTargetPerson());
				emc.beginTransaction(Review.class);
				emc.persist(targetReview);
				emc.commit();
				jobSet.add(review.getJob());
			}
		}
	}

	private void replaceTaskOrRead(String id, String job, String path, Handover handover, Set<String> jobSet) {
		try {
			ThisApplication.context().applications()
					.postQuery(x_processplatform_service_processing.class,
							Applications.joinQueryUri(path, id, "replace"), handover,
							job)
					.getData(WoId.class);
			jobSet.add(job);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private boolean hasReview(Business business, String job, String person) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		Long count = emc.countEqualAndEqual(Review.class, Review.job_FIELDNAME, job, Review.person_FIELDNAME, person);
		return count > 0;
	}

	private Review getReview(Business business, String job, String person) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		return emc.firstEqualAndEqual(Review.class, Review.job_FIELDNAME, job, Review.person_FIELDNAME, person);
	}

	private List<Read> listRead(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Read.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Read> root = cq.from(Read.class);
		Path<String> idPath = root.get(Read_.id);
		Path<String> jobPath = root.get(Read_.job);
		Predicate p = cb.equal(root.get(Read_.person), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(Read_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(Read_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme()) && ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(Read_.job).in(handover.getJobList()));
		}
		cq.multiselect(idPath, jobPath).where(p);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<Read> list = new ArrayList<>();
		for (Tuple o : os) {
			Read read = new Read();
			read.setId(o.get(idPath));
			read.setJob(o.get(jobPath));
			list.add(read);
		}
		return list;
	}

	private List<String> listReadCompleted(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(ReadCompleted_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(ReadCompleted_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())
				&& ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(ReadCompleted_.job).in(handover.getJobList()));
		}
		cq.select(root.get(ReadCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<Task> listTask(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Task.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Task> root = cq.from(Task.class);
		Path<String> idPath = root.get(Task_.id);
		Path<String> jobPath = root.get(Task_.job);
		Predicate p = cb.equal(root.get(Task_.person), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(Task_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(Task_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())
				&& ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(Task_.job).in(handover.getJobList()));
		}
		p = cb.and(p, cb.notEqual(root.get(Task_.identity), handover.getTargetIdentity()));
		cq.multiselect(idPath, jobPath).where(p);
		List<Tuple> os = em.createQuery(cq).getResultList();
		List<Task> list = new ArrayList<>();
		for (Tuple o : os) {
			Task task = new Task();
			task.setId(o.get(idPath));
			task.setJob(o.get(jobPath));
			list.add(task);
		}
		return list;
	}

	private List<String> listTaskCompleted(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.person), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(TaskCompleted_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(TaskCompleted_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())
				&& ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(TaskCompleted_.job).in(handover.getJobList()));
		}
		cq.select(root.get(TaskCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<String> listReview(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.person), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(Review_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(Review_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())
				&& ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(Review_.job).in(handover.getJobList()));
		}
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<String> listWork(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.creatorPerson), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(Work_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(Work_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())
				&& ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(Work_.job).in(handover.getJobList()));
		}
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<String> listWorkCompleted(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(WorkCompleted_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(WorkCompleted_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())
				&& ListTools.isNotEmpty(handover.getJobList())){
			p = cb.and(p, root.get(WorkCompleted_.job).in(handover.getJobList()));
		}
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<String> listDraft(Business business, Handover handover) throws Exception{
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Draft.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Draft> root = cq.from(Draft.class);
		Predicate p = cb.equal(root.get(Draft_.person), handover.getPerson());
		if(HandoverSchemeEnum.APPLICATION.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getApplicationList())){
				p = cb.and(p, root.get(Draft_.application).in(handover.getApplicationList()));
			}
		}else if(HandoverSchemeEnum.PROCESS.getValue().equals(handover.getScheme())){
			if(ListTools.isNotEmpty(handover.getProcessList())){
				List<String> processList = business.process().listEditionProcess(handover.getProcessList());
				p = cb.and(p, root.get(Draft_.process).in(processList));
			}
		}else if(HandoverSchemeEnum.JOB.getValue().equals(handover.getScheme())){
			return Collections.emptyList();
		}
		cq.select(root.get(Draft_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private List<String> list() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Handover.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Handover> root = cq.from(Handover.class);
			Predicate p = cb.equal(root.get(Handover_.status), HandoverStatusEnum.PROCESSING.getValue());
			cq.select(root.get(Handover_.id)).where(p).orderBy(cb.asc(root.get(JpaObject_.sequence)));
			return em.createQuery(cq).setMaxResults(10).getResultList();
		}
	}

}
