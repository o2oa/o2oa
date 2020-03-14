package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import javax.script.CompiledScript;
import javax.script.ScriptContext;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.SerialBuilder;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

/***
 * 
 * 实现3个时间的基础功能
 *
 */
public abstract class AbstractProcessor extends AbstractBaseProcessor {

	private static Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);

	protected AbstractProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	public String arrive(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		/* 返回值,如果返回值不为空,将继续循环 */
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			ActivityType activityType = work.getDestinationActivityType();
			if (null == activityType) {
				throw new ExceptionEmptyActivityType(work.getTitle(), work.getId(), work.getDestinationActivityType());
			}
			Activity activity = this.business().element().get(work.getDestinationActivity(),
					ActivityType.getClassOfActivityType(activityType));
			if (null == activity) {
				throw new ExceptionActivityNotExist(work.getTitle(), work.getId(), work.getDestinationActivityType(),
						work.getDestinationActivity());
			}
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingConfigurator,
					processingAttributes);
			/* 清空可能的Manual活动预期人员 */
			this.arrive_cleanManualTaskIdentityList(aeiObjects);
			/* 清空可能的Manual活动授权信息 */
			this.arrive_cleanManualEmpowerMap(aeiObjects);
//			/* 将强制路由标记进行修改 */
//			work.setForceRouteArriveCurrentActivity(false);
//			if (BooleanUtils.isTrue(work.getForceRoute())) {
//				work.setForceRoute(false);
//				work.setForceRouteArriveCurrentActivity(true);
//			}
			/* 计算是否经过人工节点 */
			this.arrive_updateWorkThroughManual(aeiObjects);
			/* 清空BeforeExecuted活动执行一次事件 */
			work.setBeforeExecuted(false);
			aeiObjects.getUpdateWorks().add(work);
			this.callBeforeArriveScript(aeiObjects);
			this.arriveActivity(aeiObjects);
			/* 创建待阅和参阅 */
			aeiObjects.getCreateReads().addAll(this.concreteRead(aeiObjects));
			aeiObjects.getCreateReviews().addAll(this.concreteReview(aeiObjects));
			/*
			 * 主方法,进行业务运行
			 */
			work = this.arriveProcessing(aeiObjects);
			/*
			 * 主方法结束
			 */
			if (null == work) {
				throw new Exception("arrvie return empty, work{id:" + workId + "}.");
			}
			if (null != aeiObjects.getProcess()) {
				if (StringUtils.equalsIgnoreCase(aeiObjects.getProcess().getSerialActivity(),
						aeiObjects.getActivity().getId())
						&& (!StringUtils.equals(aeiObjects.getProcess().getSerialPhase(),
								Process.SERIALPHASE_INQUIRE))) {
					if (StringUtils.isEmpty(work.getSerial())) {
						SerialBuilder serialBuilder = new SerialBuilder(ThisApplication.context(),
								this.entityManagerContainer(), work.getProcess(), work.getId());
						String serial = serialBuilder.concrete(aeiObjects);
						work.setSerial(serial);
					}
				}
			}
			aeiObjects.commit();
			this.arriveCommitted(aeiObjects);
			/* 运行AfterArriveScript时间 */
			this.callAfterArriveScript(aeiObjects);
			return work.getId();
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
	}

	private void arrive_cleanManualTaskIdentityList(AeiObjects aeiObjects) throws Exception {
		aeiObjects.getWork().setManualTaskIdentityList(new ArrayList<String>());
	}

	private void arrive_cleanManualEmpowerMap(AeiObjects aeiObjects) throws Exception {
		aeiObjects.getWork().getProperties().setManualEmpowerMap(new LinkedHashMap<String, String>());
	}

	private void arrive_updateWorkThroughManual(AeiObjects aeiObjects) throws Exception {
		boolean value = aeiObjects.getWorkLogs().stream().filter(o -> {
			return Objects.equals(ActivityType.manual, o.getArrivedActivityType())
					&& BooleanUtils.isTrue(o.getConnected());
		}).count() > 0;
		aeiObjects.getWork().setWorkThroughManual(value);
	}

	private void callBeforeArriveScript(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCallBeforeArriveScript()) {
			if (this.hasBeforeArriveScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				CompiledScript cs = null;
				if (this.hasBeforeArriveScript(aeiObjects.getProcess())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getProcess(), Business.EVENT_BEFOREARRIVE);
					cs.eval(scriptContext);
				}
				if (this.hasBeforeArriveScript(aeiObjects.getActivity())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getActivity(), Business.EVENT_BEFOREARRIVE);
					cs.eval(scriptContext);
				}
			}
		}
	}

	private void callAfterArriveScript(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCallAfterArriveScript()) {
			if (this.hasAfterArriveScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				CompiledScript cs = null;
				if (this.hasAfterArriveScript(aeiObjects.getProcess())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getProcess(), Business.EVENT_AFTERARRIVE);
					cs.eval(scriptContext);
				}
				if (this.hasAfterArriveScript(aeiObjects.getActivity())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getActivity(), Business.EVENT_AFTERARRIVE);
					cs.eval(scriptContext);
				}
			}
		}
	}

	private List<Read> concreteRead(AeiObjects aeiObjects) throws Exception {
		List<Read> list = new ArrayList<>();
		List<String> identities = TranslateReadIdentityTools.translate(aeiObjects);
		for (String identity : ListTools.trim(identities, true, true)) {
			String unit = this.business().organization().unit().getWithIdentity(identity);
			String person = this.business().organization().person().getWithIdentity(identity);
			Read read = new Read(aeiObjects.getWork(), identity, unit, person);
			list.add(read);
		}
		return list;
	}

	private List<Review> concreteReview(AeiObjects aeiObjects) throws Exception {
		List<Review> list = new ArrayList<>();
		List<String> people = TranslateReviewPersonTools.translate(aeiObjects);
		for (String person : ListTools.trim(people, true, true)) {
			Review review = new Review(aeiObjects.getWork(), person);
			list.add(review);
		}
		return list;
	}

	public List<String> execute(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		List<String> results = new ArrayList<>();
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
//			if (BooleanUtils.isTrue(work.getForceRoute())) {
//				/** 如果是调度那么跳过运行 */
//				results.add(work.getId());
//				return results;
//			}
			ActivityType activityType = work.getActivityType();
			if (null == activityType) {
				throw new ExceptionEmptyActivityType(work.getTitle(), work.getId(), work.getActivityType());
			}
			Activity activity = this.business().element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(activityType));
			if (null == activity) {
				throw new ExceptionActivityNotExist(work.getTitle(), work.getId(), work.getActivityType(),
						work.getActivity());
			}
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingConfigurator,
					processingAttributes);
			aeiObjects.getUpdateWorks().add(work);
			/* 如果是调度路由,需要重新设置froceRoute */
			if (BooleanUtils.isNotTrue(work.getBeforeExecuted())) {
				/* 仅执行一次BeforeExecuteScript中的代码 */
				this.callBeforeExecuteScript(aeiObjects);
				work.setBeforeExecuted(true);
			}
			/* 运行业务方法 */
			List<Work> works = this.executeProcessing(aeiObjects);

			if (ListTools.isNotEmpty(works)) {
				for (Work o : works) {
					results.add(o.getId());
				}
			}
			aeiObjects.commit();
			this.executeCommitted(aeiObjects);
			/** 发送在队列中的待办消息, 待办消息必须在数据提交后发送,否则会不到待办 */
			if (ListTools.isNotEmpty(works)) {
				/** 已经有返回的work将要离开当前环节,执行AfterExecuteScript中的代码 */
				this.callAfterExecuteScript(aeiObjects);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return results;
	}

	private void callBeforeExecuteScript(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCallBeforeExecuteScript()) {
			if (this.hasBeforeExecuteScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				CompiledScript cs = null;
				if (this.hasBeforeExecuteScript(aeiObjects.getProcess())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getProcess(), Business.EVENT_BEFOREEXECUTE);
					cs.eval(scriptContext);
				}
				if (this.hasBeforeExecuteScript(aeiObjects.getActivity())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getActivity(), Business.EVENT_BEFOREEXECUTE);
					cs.eval(scriptContext);
				}
			}
		}
	}

	private void callAfterExecuteScript(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCallAfterExecuteScript()) {
			if (this.hasAfterExecuteScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				CompiledScript cs = null;
				if (this.hasAfterExecuteScript(aeiObjects.getProcess())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getProcess(), Business.EVENT_AFTEREXECUTE);
					cs.eval(scriptContext);
				}
				if (this.hasAfterExecuteScript(aeiObjects.getActivity())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getActivity(), Business.EVENT_AFTEREXECUTE);
					cs.eval(scriptContext);
				}
			}
		}
	}

	public List<String> inquire(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		List<String> results = new ArrayList<>();
		try {
			Work work = this.entityManagerContainer().find(workId, Work.class);
			if (null == work) {
				throw new ExceptionWorkNotExist(workId);
			}
			ActivityType activityType = work.getActivityType();
			if (null == activityType) {
				throw new ExceptionEmptyActivityType(work.getTitle(), work.getId(), work.getActivityType());
			}
			Activity activity = this.business().element().get(work.getActivity(),
					ActivityType.getClassOfActivityType(activityType));
			if (null == activity) {
				throw new ExceptionActivityNotExist(work.getTitle(), work.getId(), work.getActivityType(),
						work.getActivity());
			}
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingConfigurator,
					processingAttributes);
			aeiObjects.getUpdateWorks().add(work);
//			if (BooleanUtils.isTrue(work.getForceRoute())) {
//				/** 如果是调度那么跳过运行 */
//				results.add(work.getId());
//				return results;
//			}
			/* 运行查询路由前脚本 */
			this.callBeforeInquireScript(aeiObjects);
			/*
			 * 运行主方法
			 */
			List<Route> selectRoutes = this.inquireProcessing(aeiObjects);
			/*
			 * 主方法运行完成
			 */
			aeiObjects.addSelectRoutes(selectRoutes);
			if ((null == selectRoutes) || selectRoutes.isEmpty()) {
				throw new Exception("inquire return empty routes");
			}
			List<Work> works = new ArrayList<>();
			/** 运行查询路由后脚本 */
			work.setDestinationActivity(selectRoutes.get(0).getActivity());
			work.setDestinationActivityType(selectRoutes.get(0).getActivityType());
			work.setDestinationRoute(selectRoutes.get(0).getId());
			work.setDestinationRouteName(selectRoutes.get(0).getName());
			works.add(work);
			// }
			for (Work o : works) {
				results.add(o.getId());
			}
			if (null != aeiObjects.getProcess()) {
				if (StringUtils.equalsIgnoreCase(aeiObjects.getProcess().getSerialActivity(),
						aeiObjects.getActivity().getId())
						&& (StringUtils.equals(aeiObjects.getProcess().getSerialPhase(),
								Process.SERIALPHASE_INQUIRE))) {
					if (StringUtils.isEmpty(work.getSerial())) {
						SerialBuilder serialBuilder = new SerialBuilder(ThisApplication.context(),
								this.entityManagerContainer(), work.getProcess(), work.getId());
						String serial = serialBuilder.concrete(aeiObjects);
						work.setSerial(serial);
					}
				}
			}
			aeiObjects.commit();
			this.inquireCommitted(aeiObjects);
			/** 运行 AfterInquireScript事件 */
			this.callAfterInquireScript(aeiObjects);
		} catch (Exception e) {
			logger.error(e);
		}
		return results;
	}

	private void callBeforeInquireScript(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCallBeforeInquireScript()) {
			if (this.hasBeforeInquireScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				CompiledScript cs = null;
				if (this.hasBeforeInquireScript(aeiObjects.getProcess())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getProcess(), Business.EVENT_BEFOREINQUIRE);
					cs.eval(scriptContext);
				}
				if (this.hasBeforeInquireScript(aeiObjects.getActivity())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getActivity(), Business.EVENT_BEFOREINQUIRE);
					cs.eval(scriptContext);
				}
			}
		}
	}

	private void callAfterInquireScript(AeiObjects aeiObjects) throws Exception {
		if (aeiObjects.getActivityProcessingConfigurator().getCallAfterInquireScript()) {
			if (this.hasAfterInquireScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
				ScriptContext scriptContext = aeiObjects.scriptContext();
				CompiledScript cs = null;
				if (this.hasAfterInquireScript(aeiObjects.getProcess())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getProcess(), Business.EVENT_AFTERINQUIRE);
					cs.eval(scriptContext);
				}
				if (this.hasAfterInquireScript(aeiObjects.getActivity())) {
					cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
							aeiObjects.getActivity(), Business.EVENT_AFTERINQUIRE);
					cs.eval(scriptContext);
				}
			}
		}
	}

	protected abstract Work arriveProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void arriveCommitted(AeiObjects aeiObjects) throws Exception;

	protected abstract List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void executeCommitted(AeiObjects aeiObjects) throws Exception;

	protected abstract List<Route> inquireProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void inquireCommitted(AeiObjects aeiObjects) throws Exception;

}