//package com.x.processplatform.service.processing;
//
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import javax.persistence.EntityManager;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//
//import org.apache.commons.collections4.list.UnmodifiableList;
//import org.apache.commons.lang3.StringUtils;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.annotation.CheckPersistType;
//import com.x.base.core.project.Applications;
//import com.x.base.core.project.x_processplatform_service_processing;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.processplatform.core.entity.content.Record;
//import com.x.processplatform.core.entity.content.RecordProperties.NextManual;
//import com.x.processplatform.core.entity.content.Task;
//import com.x.processplatform.core.entity.content.TaskCompleted;
//import com.x.processplatform.core.entity.content.TaskCompleted_;
//import com.x.processplatform.core.entity.content.Task_;
//import com.x.processplatform.core.entity.content.WorkCompleted;
//import com.x.processplatform.core.entity.content.WorkLog;
//
//public class RecordBuilder {
//
//	private static final List<String> TASK_FETCH_FIELDS = UnmodifiableList
//			.unmodifiableList(Arrays.asList(Task.identity_FIELDNAME, Task.job_FIELDNAME, Task.work_FIELDNAME,
//					Task.activity_FIELDNAME, Task.activityAlias_FIELDNAME, Task.activityName_FIELDNAME,
//					Task.activityToken_FIELDNAME, Task.activityType_FIELDNAME, Task.identity_FIELDNAME));
//
//	private RecordBuilder() {
//		// nothing
//	}
//
//	public static Record ofTaskProcessing(String recordType, String workLogId, String taskCompletedId, String series)
//			throws Exception {
//		Record rec = null;
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			emc.beginTransaction(Record.class);
//			emc.beginTransaction(Task.class);
//			Business business = new Business(emc);
//			WorkLog workLog = emc.find(workLogId, WorkLog.class);
//			TaskCompleted taskCompleted = emc.find(taskCompletedId, TaskCompleted.class);
//			if ((null != workLog) && (null != taskCompleted)) {
//				rec = new Record(workLog);
//				rec.setIdentity(taskCompleted.getIdentity());
//				rec.setPerson(taskCompleted.getPerson());
//				rec.setUnit(taskCompleted.getUnit());
//				rec.setRouteName(taskCompleted.getRouteName());
//				rec.setOpinion(taskCompleted.getOpinion());
//				rec.setMediaOpinion(taskCompleted.getMediaOpinion());
//				rec.setStartTime(taskCompleted.getStartTime());
//				rec.setEmpowerFromIdentity(taskCompleted.getEmpowerFromIdentity());
//				rec.setType(recordType);
//				// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
//				checkIfWorkAlreadyCompleted(business, rec, workLog.getJob());
//				fillIdentityAndUnit(business, rec);
//				elapsed(rec);
//				// 已经存在的已办理人员.
//				List<String> identities = listJoinInquireTaskCompletedIdentityWithActivityToken(business,
//						workLog.getJob(), workLog.getFromActivityToken());
//				List<Task> tasks = listTaskWithSeriesOrActivityToken(business, workLog.getJob(),
//						workLog.getFromActivityToken(), series);
//				tasks.stream().forEach(o -> {
//					o.setPrevTaskIdentityList(identities);
//					o.setPrevTaskIdentity(taskCompleted.getIdentity());
//				});
//				List<NextManual> nextManuals = tasks.stream()
//						.collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
//						.map(en -> {
//							Optional<Task> opt = en.getValue().stream().findFirst();
//							if (opt.isPresent()) {
//								NextManual nextManual = new NextManual();
//								nextManual.setActivity(opt.get().getActivity());
//								nextManual.setActivityAlias(opt.get().getActivityAlias());
//								nextManual.setActivityName(opt.get().getActivityName());
//								nextManual.setActivityToken(opt.get().getActivityToken());
//								nextManual.setActivityType(opt.get().getActivityType());
//								nextManual.setTaskIdentityList(
//										en.getValue().stream().map(Task::getIdentity).collect(Collectors.toList()));
//								return nextManual;
//							}
//							return null;
//						}).filter(Objects::nonNull).collect(Collectors.toList());
//				rec.setNextManualList(nextManuals);
//				rec.setNextManualTaskIdentityList(
//						tasks.stream().map(Task::getIdentity).distinct().collect(Collectors.toList()));
//				emc.persist(rec, CheckPersistType.all);
//				emc.commit();
//			}
//			return rec;
//		}
//	}
//
//	public static Record ofWorkProcessing(String recordType, String workLogId, String identity, String series)
//			throws Exception {
//		Record rec = null;
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			emc.beginTransaction(Record.class);
//			emc.beginTransaction(Task.class);
//			Business business = new Business(emc);
//			WorkLog workLog = emc.find(workLogId, WorkLog.class);
//			if ((null != workLog)) {
//				rec = new Record(workLog);
//				if (StringUtils.isNotBlank(identity)) {
//					TaskCompleted existTaskCompleted = emc.firstEqualAndEqual(TaskCompleted.class,
//							TaskCompleted.job_FIELDNAME, workLog.getJob(), TaskCompleted.identity_FIELDNAME, identity);
//					if (null != existTaskCompleted) {
//						rec.setIdentity(existTaskCompleted.getIdentity());
//						rec.setUnit(existTaskCompleted.getUnit());
//						rec.setPerson(existTaskCompleted.getPerson());
//					} else {
//						rec.setIdentity(identity);
//						rec.setUnit(business.organization().unit().getWithIdentity(identity));
//						rec.setPerson(business.organization().person().getWithIdentity(identity));
//					}
//				}
//				rec.setType(recordType);
//				// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
//				checkIfWorkAlreadyCompleted(business, rec, workLog.getJob());
//				fillIdentityAndUnit(business, rec);
//				elapsed(rec);
//				// 已经存在的已办理人员.
//				List<String> identities = listJoinInquireTaskCompletedIdentityWithActivityToken(business,
//						workLog.getJob(), workLog.getFromActivityToken());
//				List<Task> tasks = listTaskWithSeriesOrActivityToken(business, workLog.getJob(),
//						workLog.getFromActivityToken(), series);
//				tasks.stream().forEach(o -> {
//					o.setPrevTaskIdentityList(identities);
//					o.setPrevTaskIdentity(identity);
//				});
//				List<NextManual> nextManuals = tasks.stream()
//						.collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
//						.map(en -> {
//							Optional<Task> opt = en.getValue().stream().findFirst();
//							if (opt.isPresent()) {
//								NextManual nextManual = new NextManual();
//								nextManual.setActivity(opt.get().getActivity());
//								nextManual.setActivityAlias(opt.get().getActivityAlias());
//								nextManual.setActivityName(opt.get().getActivityName());
//								nextManual.setActivityToken(opt.get().getActivityToken());
//								nextManual.setActivityType(opt.get().getActivityType());
//								nextManual.setTaskIdentityList(
//										en.getValue().stream().map(Task::getIdentity).collect(Collectors.toList()));
//								return nextManual;
//							}
//							return null;
//						}).filter(Objects::nonNull).collect(Collectors.toList());
//				rec.setNextManualList(nextManuals);
//				rec.setNextManualTaskIdentityList(
//						tasks.stream().map(Task::getIdentity).distinct().collect(Collectors.toList()));
//				emc.persist(rec, CheckPersistType.all);
//				emc.commit();
//			}
//			return rec;
//		}
//	}
//
//	private static List<String> listJoinInquireTaskCompletedIdentityWithActivityToken(Business business, String job,
//			String activityToken) throws Exception {
//		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
//		Predicate p = cb.equal(root.get(TaskCompleted_.job), job);
//		p = cb.and(p, cb.or(cb.equal(root.get(TaskCompleted_.activityToken), activityToken),
//				cb.equal(root.get(TaskCompleted_.joinInquire), Boolean.TRUE)));
//		return em.createQuery(cq.select(root.get(TaskCompleted_.identity)).where(p)).getResultList().stream().distinct()
//				.collect(Collectors.toList());
//	}
//
//	/**
//	 * 获取所有待办
//	 * 
//	 * @param business
//	 * @param job
//	 * @param activityToken
//	 * @param series
//	 * @return
//	 * @throws Exception
//	 */
//	private static List<Task> listTaskWithSeriesOrActivityToken(Business business, String job, String activityToken,
//			String series) throws Exception {
//		EntityManager em = business.entityManagerContainer().get(Task.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Task> cq = cb.createQuery(Task.class);
//		Root<Task> root = cq.from(Task.class);
//		Predicate p = cb.equal(root.get(Task_.job), job);
//		p = cb.and(p, cb.or(cb.equal(root.get(Task_.activityToken), activityToken),
//				cb.equal(root.get(Task_.series), series)));
//		return em.createQuery(cq.where(p)).getResultList();
//	}
//
////	public static Record ofTaskProcessing(String recordType, WorkLog workLog, Task task, String series)
////			throws Exception {
////		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
////			Business business = new Business(emc);
////			Record rec = new Record(workLog, task);
////			rec.setType(recordType);
////			// 校验workCompleted,如果存在,那么说明工作已经完成,标识状态为已经完成.
////			checkIfWorkAlreadyCompleted(business, rec, workLog.getJob());
////			fillIdentityAndUnit(business, rec);
////			elapsed(rec);
//////			TaskCompleted taskCompleted = emc.find(taskCompletedId, TaskCompleted.class);
//////			if (null != taskCompleted) {
//////				// 处理完成后在重新写入待办信息
//////				rec.getProperties().setOpinion(taskCompleted.getOpinion());
//////				rec.getProperties().setRouteName(taskCompleted.getRouteName());
//////				rec.getProperties().setMediaOpinion(taskCompleted.getMediaOpinion());
//////			}
////			List<Task> tasks = emc.fetchEqualAndEqual(Task.class, TASK_FETCH_FIELDS, Task.job_FIELDNAME,
////					workLog.getJob(), Task.series_FIELDNAME, series);
////			List<NextManual> nextManuals = tasks.stream()
////					.collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
////					.map(en -> {
////						Optional<Task> opt = en.getValue().stream().findFirst();
////						if (opt.isPresent()) {
////							NextManual nextManual = new NextManual();
////							nextManual.setActivity(opt.get().getActivity());
////							nextManual.setActivityAlias(opt.get().getActivityAlias());
////							nextManual.setActivityName(opt.get().getActivityName());
////							nextManual.setActivityToken(opt.get().getActivityToken());
////							nextManual.setActivityType(opt.get().getActivityType());
////							nextManual.setTaskIdentityList(
////									en.getValue().stream().map(Task::getIdentity).collect(Collectors.toList()));
////							return nextManual;
////						}
////						return null;
////					}).filter(Objects::nonNull).collect(Collectors.toList());
////			rec.setNextManualList(nextManuals);
////			rec.setNextManualTaskIdentityList(tasks.stream().map(Task::getIdentity).collect(Collectors.toList()));
////			return rec;
////		}
////	}
////
////	public static Record ofWorkProcessing(String recordType, WorkLog workLog, EffectivePerson effectivePerson,
////			String series) throws Exception {
////		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
////			Business business = new Business(emc);
////			Record rec = new Record(workLog);
////			rec.setType(recordType);
////			checkIfWorkAlreadyCompleted(business, rec, workLog.getJob());
////			// 需要记录处理人,先查看当前用户有没有之前处理过的信息,如果没有,取默认身份
////			if (null != effectivePerson) {
////				TaskCompleted existTaskCompleted = emc.firstEqualAndEqual(TaskCompleted.class,
////						TaskCompleted.job_FIELDNAME, workLog.getJob(), TaskCompleted.person_FIELDNAME,
////						effectivePerson.getDistinguishedName());
////				if (null != existTaskCompleted) {
////					rec.setIdentity(existTaskCompleted.getIdentity());
////					rec.setUnit(existTaskCompleted.getUnit());
////				} else {
////					rec.setIdentity(business.organization().identity()
////							.getMajorWithPerson(effectivePerson.getDistinguishedName()));
////					rec.setUnit(business.organization().unit().getWithIdentity(rec.getIdentity()));
////				}
////				rec.setPerson(effectivePerson.getDistinguishedName());
////			}
////			fillIdentityAndUnit(business, rec);
////			elapsed(rec);
////			List<Task> tasks = emc.fetchEqualAndEqual(Task.class, TASK_FETCH_FIELDS, Task.job_FIELDNAME,
////					workLog.getJob(), Task.series_FIELDNAME, series);
////			List<NextManual> nextManuals = tasks.stream()
////					.collect(Collectors.groupingBy(Task::getActivity, Collectors.toList())).entrySet().stream()
////					.map(en -> {
////						Optional<Task> opt = en.getValue().stream().findFirst();
////						if (opt.isPresent()) {
////							NextManual nextManual = new NextManual();
////							nextManual.setActivity(opt.get().getActivity());
////							nextManual.setActivityAlias(opt.get().getActivityAlias());
////							nextManual.setActivityName(opt.get().getActivityName());
////							nextManual.setActivityToken(opt.get().getActivityToken());
////							nextManual.setActivityType(opt.get().getActivityType());
////							nextManual.setTaskIdentityList(
////									en.getValue().stream().map(Task::getIdentity).collect(Collectors.toList()));
////							return nextManual;
////						}
////						return null;
////					}).filter(Objects::nonNull).collect(Collectors.toList());
////			rec.setNextManualList(nextManuals);
////			rec.setNextManualTaskIdentityList(tasks.stream().map(Task::getIdentity).collect(Collectors.toList()));
////			return rec;
////		}
////	}
//
//	private static void elapsed(Record rec) throws Exception {
//		rec.getProperties()
//				.setElapsed(Config.workTime().betweenMinutes(rec.getProperties().getStartTime(), rec.getRecordTime()));
//	}
//
//	private static boolean checkIfWorkAlreadyCompleted(Business business, Record rec, String job) throws Exception {
//		WorkCompleted workCompleted = business.entityManagerContainer().firstEqual(WorkCompleted.class,
//				WorkCompleted.job_FIELDNAME, job);
//		if (null != workCompleted) {
//			rec.setCompleted(true);
//			rec.setWorkCompleted(workCompleted.getId());
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	private static void fillIdentityAndUnit(Business business, Record rec) throws Exception {
//		// 获取在record中需要记录的task中身份所有的组织职务.
//		rec.getProperties().setUnitDutyList(business.organization().unitDuty().listNameWithIdentity(rec.getIdentity()));
//		// 记录处理身份的排序号
//		rec.getProperties().setIdentityOrderNumber(
//				business.organization().identity().getOrderNumber(rec.getIdentity(), Integer.MAX_VALUE));
//		// 记录处理身份所在组织的排序号
//		rec.getProperties()
//				.setUnitOrderNumber(business.organization().unit().getOrderNumber(rec.getUnit(), Integer.MAX_VALUE));
//		// 记录处理身份所在组织层级组织排序号
//		rec.getProperties()
//				.setUnitLevelOrderNumber(business.organization().unit().getLevelOrderNumber(rec.getUnit(), ""));
//		// 记录处理人身份所在目录的层级名.
//		rec.getProperties().setUnitLevelName(business.organization().unit().getLevelName(rec.getUnit(), ""));
//	}
//
//	public static Record ofTaskEmpower(Task task, String empowerFromPerson, String empowerFromUnit) {
//		Record o = new Record();
//		o.setType(Record.TYPE_EMPOWER);
//		o.setApplication(task.getApplication());
//		o.setProcess(task.getProcess());
//		o.setJob(task.getJob());
//		o.setCompleted(false);
//		o.setWork(task.getWork());
//		o.setFromActivity(task.getActivity());
//		o.setFromActivityAlias(task.getActivityAlias());
//		o.setFromActivityName(task.getActivityName());
//		o.setFromActivityToken(task.getActivityToken());
//		o.setFromActivityType(task.getActivityType());
////		o.setArrivedActivity(task.getActivity());
////		o.setArrivedActivityAlias(task.getActivityAlias());
////		o.setArrivedActivityName(task.getActivityName());
////		o.setArrivedActivityToken(task.getActivityToken());
////		o.setArrivedActivityType(task.getActivityType());
//		o.getProperties().setEmpowerToPerson(task.getPerson());
//		o.getProperties().setEmpowerToIdentity(task.getIdentity());
//		o.getProperties().setEmpowerToUnit(task.getUnit());
//		o.setIdentity(task.getEmpowerFromIdentity());
//		o.setPerson(empowerFromPerson);
//		o.setUnit(empowerFromUnit);
//		o.getProperties().setElapsed(0L);
//		NextManual nextManual = new NextManual();
//		nextManual.setActivity(task.getActivity());
//		nextManual.setActivityAlias(task.getActivityAlias());
//		nextManual.setActivityName(task.getActivityName());
//		nextManual.setActivityToken(task.getActivityToken());
//		nextManual.setActivityType(task.getActivityType());
//		o.getProperties().getNextManualList().add(nextManual);
//		// o.getProperties().getNextManualTaskIdentityList().add(task.getIdentity());
//		return o;
//	}
//
//	public static String processing(Record r) throws Exception {
//		WoId resp = ThisApplication.context().applications()
//				.postQuery(false, x_processplatform_service_processing.class,
//						Applications.joinQueryUri("record", "job", r.getJob()), r, r.getJob())
//				.getData(WoId.class);
//		if (StringUtils.isBlank(resp.getId())) {
//			throw new ExceptionRecordProcessing(r.getWork());
//		} else {
//			return resp.getId();
//		}
//	}
//
//}
