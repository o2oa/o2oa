package com.x.processplatform.service.processing.processor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.serial.builder.SerialBuilder;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.WorkDataHelper;
import com.x.processplatform.service.processing.configurator.ActivityProcessingConfigurator;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public abstract class AbstractProcessor extends AbstractTaskProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

	protected AbstractProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	public String arrive(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes attributes) {
		/* 返回值,如果返回值不为空将继续循环 */
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class, ExceptionWhen.not_found, true);
			Process process = this.business().element().get(work.getProcess(), Process.class);
			ActivityType activityType = work.getDestinationActivityType();
			Activity activity = this.business().element().get(work.getDestinationActivity(),
					ActivityType.getClassOfActivityType(activityType));
			/* 清空可能的Manual活动预期人员 */
			work.setManualTaskIdentityList(new ArrayList<String>());
			/* 将强制路由标记进行修改 */
			work.setForceRoute(false);
			WorkDataHelper workDataHelper = new WorkDataHelper(this.entityManagerContainer(), work);
			Data data = workDataHelper.get();
			ActivityProcessingConfigurator activityConfigurator = processingConfigurator.get(activityType);
			this.callBeforeArriveScript(activityConfigurator, attributes, activity, work, data);
			this.arriveActivity(activityConfigurator, work, activity);
			/* 创建待阅 */
			this.concreteRead(attributes, work, data, activity);
			/* 创建参阅 */
			this.concreteReview(attributes, work, data, activity);
			logger.debug("arrive work title:{}, id:{}, actvity name:{}, id:{}, process name:{}, id{}.", work.getTitle(),
					work.getId(), activity.getName(), activity.getId(), work.getProcessName(), work.getProcess());
			work = this.arriveProcessing(processingConfigurator, attributes, work, data, activity);
			if (null == work) {
				throw new Exception("arrvie return empty, work{id:" + workId + "}.");
			}
			if (null != process) {
				if (StringUtils.equalsIgnoreCase(process.getSerialActivity(), activity.getId())) {
					if (StringUtils.isEmpty(work.getSerial())) {
						SerialBuilder serialBuilder = new SerialBuilder(this.entityManagerContainer(),
								work.getProcess(), work.getId());
						String serial = serialBuilder.concrete();
						work.setSerial(serial);
						logger.debug(
								"concrete serial:{}, work title:{}, id:{}, actvity name:{}, id:{}, process name:{}, id{}.",
								serial, work.getTitle(), work.getId(), activity.getName(), activity.getId(),
								work.getProcessName(), work.getProcess());
					}
				}
			}
			callAfterArriveScript(activityConfigurator, attributes, activity, work, data);
			workDataHelper.update(data);
			this.entityManagerContainer().commit();
			return work.getId();
		} catch (Exception e) {
			this.logProcessingError(workId, e);
			return null;
		}

	}

	private void callAfterArriveScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, Work work, Data data)
			throws Exception, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (activityConfigurator.getCallAfterArriveScript()) {
			if (this.hasAfterArriveScript(activity)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity);
				scriptHelper.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, AAS)),
						Objects.toString(PropertyUtils.getProperty(activity, AAST)));
			}
		}
	}

	private void callBeforeArriveScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, Work work, Data data)
			throws Exception, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (activityConfigurator.getCallBeforeArriveScript()) {
			/* 如果需要运行到达前脚本 */
			if (this.hasBeforeArriveScript(activity)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity);
				scriptHelper.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, BAS)),
						Objects.toString(PropertyUtils.getProperty(activity, BAST)));
			}
		}
	}

	private void concreteRead(ProcessingAttributes attributes, Work work, Data data, Activity activity)
			throws Exception {
		List<String> identities = TranslateReadIdentityTools.translate(this.business(), attributes, work, data,
				activity);
		identities = this.business().organization().identity().check(identities);
		for (String o : ListTools.trim(identities, true, true)) {
			this.createRead(o, work);
		}
	}

	private void concreteReview(ProcessingAttributes attributes, Work work, Data data, Activity activity)
			throws Exception {
		List<String> identities = TranslateReviewIdentityTools.translate(this.business(), attributes, work, data,
				activity);
		identities = this.business().organization().identity().check(identities);
		for (String o : ListTools.trim(identities, true, true)) {
			this.createReview(o, work);
		}
	}

	public List<String> execute(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes attributes) {
		List<String> results = new ArrayList<>();
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class, ExceptionWhen.not_found);
			if (BooleanUtils.isTrue(work.getForceRoute())) {
				/** 如果是调度那么跳过运行 */
				results.add(work.getId());
				return results;
			}
			ActivityType activityType = work.getActivityType();
			Activity activity = this.business().element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(activityType));
			logger.info("{} execute work{id:{}, activityName:{}, activityId:{}}.", this.getClass().getSimpleName(),
					workId, activity.getName(), activity.getId());
			if (BooleanUtils.isTrue(work.getExecuted())) {
				/** 如果是已经运行过的，那么直接返回 */
				results.add(work.getId());
				return results;
			}
			/** 需要运行开启事务 */
			this.entityManagerContainer().beginTransaction(Work.class);
			Process process = this.business().element().get(work.getProcess(), Process.class);
			ActivityProcessingConfigurator activityConfigurator = processingConfigurator.get(activityType);
			WorkDataHelper workDataHelper = new WorkDataHelper(this.entityManagerContainer(), work);
			Data data = workDataHelper.get();
			/**
			 * 运行执行前脚本<br>
			 * manul环节单独判断 如果没有运行过beforeArrivedExecuteScript那么运行脚本
			 */
			this.callBeforeArrivedExecuteScript(activityConfigurator, attributes, activity, work, data);
			this.callBeforeExecuteScript(activityConfigurator, attributes, activity, work, data);
			logger.debug("execute work title:{}, id:{}, actvity name:{}, id:{}, process name:{}, id{}.",
					work.getTitle(), work.getId(), activity.getName(), activity.getId(), work.getProcessName(),
					work.getProcess());
			List<Work> works = this.executeProcessing(processingConfigurator, attributes, work, data, activity);
			/**
			 * manual环节单独判断<br>
			 * 如果没有运行过afterArrivedExecuteScript那么运行脚本
			 */
			this.callAfterArrivedExecuteScript(activityConfigurator, attributes, activity, work, data);
			this.callAfterExecuteScript(activityConfigurator, attributes, activity, work, data);
			if (ListTools.isNotEmpty(works)) {
				work.setExecuted(true);
				for (Work o : works) {
					results.add(o.getId());
				}
			}
			this.calculateExpire(activityConfigurator, work, process, activity, attributes, data);
			if (activityConfigurator.getUpdateData()) {
				/** 如果是cancel或者end 那么data已经归档或者删除，就不需要updateData */
				workDataHelper.update(data);
			}
			this.entityManagerContainer().commit();
			/* 发送在队列中的待办消息, 待办消息必须在数据提交后发送,否则会不到待办 */
			this.sendTaskMessageInQueue();
		} catch (Exception e) {
			this.logProcessingError(workId, e);
		}
		return results;
	}

	private void callAfterExecuteScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, Work work, Data data)
			throws Exception, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (activityConfigurator.getCallAfterExecuteScript()) {
			if (this.hasAfterExecuteScript(activity)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity);
				scriptHelper.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, AES)),
						Objects.toString(PropertyUtils.getProperty(activity, AEST)));
			}
		}
	}

	private void callBeforeExecuteScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, Work work, Data data)
			throws Exception, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (activityConfigurator.getCallBeforeExecuteScript()) {
			if (this.hasBeforeExecuteScript(activity)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity);
				scriptHelper.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, BES)),
						Objects.toString(PropertyUtils.getProperty(activity, BEST)));
			}
		}
	}

	private void callBeforeArrivedExecuteScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, Work work, Data data) throws Exception {
		if (activityConfigurator.getCallBeforeArrivedExecuteScript()
				&& BooleanUtils.isNotTrue(work.getArrivedExecuted()) && this.hasBeforeArrivedExecuteScript(activity)) {
			ScriptHelper sh = ScriptHelperFactory.create(this.business(), attributes, work, data, activity);
			sh.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, BAES)),
					Objects.toString(PropertyUtils.getProperty(activity, BAEST)));
		}
	}

	private void callAfterArrivedExecuteScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, Work work, Data data) throws Exception {
		if (activityConfigurator.getCallAfterArrivedExecuteScript() && BooleanUtils.isNotTrue(work.getArrivedExecuted())
				&& this.hasAfterArrivedExecuteScript(activity)) {
			ScriptHelper sh = ScriptHelperFactory.create(this.business(), attributes, work, data, activity);
			sh.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, AAES)),
					Objects.toString(PropertyUtils.getProperty(activity, AAEST)));
		}
	}

	public List<String> inquire(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes attributes) {
		List<String> results = new ArrayList<>();
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class, ExceptionWhen.not_found);
			if (BooleanUtils.isTrue(work.getForceRoute())) {
				/* 如果是调度那么跳过运行 */
				results.add(work.getId());
				return results;
			}
			ActivityType activityType = work.getActivityType();
			Activity activity = this.business().element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(activityType));
			logger.debug("{} inquiry work:{}, activity:{}.", this.getClass().getSimpleName(), workId,
					activity.getName());
			if (BooleanUtils.isTrue(work.getInquired())) {
				results.add(work.getId());
				return results;
			}
			/* 需要运行 */
			this.entityManagerContainer().beginTransaction(Work.class);

			ActivityProcessingConfigurator activityConfigurator = processingConfigurator.get(activityType);
			List<Route> routes = this.business().element().listRouteWithActvity(work.getActivity(), activityType);
			WorkDataHelper workDataHelper = new WorkDataHelper(this.entityManagerContainer(), work);
			Data data = workDataHelper.get();
			/* 运行查询路由前脚本 */
			this.callBeforeInquireScript(activityConfigurator, attributes, activity, routes, work, data);
			logger.debug("inquire work title:{}, id:{}, actvity name:{}, id:{}, process name:{}, id{}.",
					work.getTitle(), work.getId(), activity.getName(), activity.getId(), work.getProcessName(),
					work.getProcess());
			List<Route> selectedRoutes = this.inquireProcessing(processingConfigurator, attributes, work, data,
					activity, routes);
			if ((null == selectedRoutes) || selectedRoutes.isEmpty()) {
				throw new Exception("inquire return empty routes");
			}
			List<Work> works = new ArrayList<>();
			/* 运行查询路由后脚本 */
			callAfterInquireScript(activityConfigurator, attributes, activity, selectedRoutes, work, data);
			if (activity.getActivityType().equals(ActivityType.parallel)) {
				for (int i = 0; i < selectedRoutes.size(); i++) {
					Route r = selectedRoutes.get(i);
					Work w;
					if (i == 0) {
						w = work;
					} else {
						w = new Work();
						work.copyTo(w, JpaObject.ID_DISTRIBUTEFACTOR);
						this.entityManagerContainer().persist(w, CheckPersistType.all);
					}
					w.setDestinationActivity(r.getActivity());
					w.setDestinationActivityType(r.getActivityType());
					w.setDestinationRoute(r.getId());
					w.setDestinationRouteName(r.getName());
					// w.setSplitRoute(r.getId());
					works.add(w);
				}
			} else {
				work.setDestinationActivity(selectedRoutes.get(0).getActivity());
				work.setDestinationActivityType(selectedRoutes.get(0).getActivityType());
				work.setDestinationRoute(selectedRoutes.get(0).getId());
				work.setDestinationRouteName(selectedRoutes.get(0).getName());
				work.setInquired(true);
				works.add(work);
			}
			for (Work o : works) {
				results.add(o.getId());
			}
			workDataHelper.update(data);
			this.entityManagerContainer().commit();
		} catch (Exception e) {
			this.logProcessingError(workId, e);
		}
		return results;
	}

	private void callAfterInquireScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, List<Route> selectedRoutes, Work work, Data data)
			throws Exception, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (activityConfigurator.getCallAfterInquireScript()) {
			if (this.hasAfterInquireScript(activity)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity, new BindingPair(Binding_name_routes, selectedRoutes));
				scriptHelper.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, AIS)),
						Objects.toString(PropertyUtils.getProperty(activity, AIST)));
			}
		}
	}

	private void callBeforeInquireScript(ActivityProcessingConfigurator activityConfigurator,
			ProcessingAttributes attributes, Activity activity, List<Route> routes, Work work, Data data)
			throws Exception, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (activityConfigurator.getCallBeforeInquireScript()) {
			if (this.hasBeforeInquireScript(activity)) {
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity, new BindingPair(Binding_name_routes, routes));
				scriptHelper.eval(work.getApplication(), Objects.toString(PropertyUtils.getProperty(activity, BIS)),
						Objects.toString(PropertyUtils.getProperty(activity, BIST)));
			}
		}
	}

	protected abstract Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception;

	protected abstract List<Work> executeProcessing(ProcessingConfigurator configurator,
			ProcessingAttributes attributes, Work work, Data data, Activity activity) throws Exception;

	protected abstract List<Route> inquireProcessing(ProcessingConfigurator configurator,
			ProcessingAttributes attributes, Work work, Data data, Activity activity, List<Route> routes)
			throws Exception;

}