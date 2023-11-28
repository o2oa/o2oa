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
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessor.class);

	protected AbstractProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	public String arrive(String workId, ProcessingConfigurator processingConfigurator,
			ProcessingAttributes processingAttributes) {
		// 返回值,如果返回值不为空,将继续循环
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
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingAttributes);
			// 清空可能的Manual活动预期人员
			this.arriveCleanManualTaskIdentityMatrix(aeiObjects);
			// 清空可能的Manual活动授权信息
			this.arriveCleanManualEmpowerMap(aeiObjects);
			// 计算是否经过人工节点
			this.arriveUpdateWorkThroughManual(aeiObjects);
			// 清空BeforeExecuted活动执行一次事件
			work.setBeforeExecuted(false);
			aeiObjects.getUpdateWorks().add(work);
			this.callBeforeArriveScript(aeiObjects);
			this.arriveActivity(aeiObjects);
			// 创建待阅和参阅
			aeiObjects.getCreateReads().addAll(this.concreteRead(aeiObjects));
			aeiObjects.getCreateReviews().addAll(this.concreteReview(aeiObjects));
			// 主方法,进行业务运行
			work = this.arriveProcessing(aeiObjects);
			// 主方法结束
			if (null == work) {
				throw new IllegalStateException("arrvie return empty, work{id:" + workId + "}.");
			}
			if ((null != aeiObjects.getProcess())
					&& StringUtils.equalsIgnoreCase(aeiObjects.getProcess().getSerialActivity(),
							aeiObjects.getActivity().getId())
					&& (!StringUtils.equals(aeiObjects.getProcess().getSerialPhase(), Process.SERIALPHASE_INQUIRE))
					&& StringUtils.isEmpty(work.getSerial())) {
				SerialBuilder serialBuilder = new SerialBuilder(ThisApplication.context(),
						this.entityManagerContainer(), work.getProcess(), work.getId());
				String serial = serialBuilder.concrete(aeiObjects);
				work.setSerial(serial);
			}
			aeiObjects.commit();
			this.arriveCommitted(aeiObjects);
			// 运行AfterArriveScript事件
			if (this.callAfterArriveScript(aeiObjects) && aeiObjects.commitData()) {
				// 执行AfterArriveScript中的代码可能修改了data数据.
				aeiObjects.entityManagerContainer().commit();
			}
			return work.getId();
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}

	private void arriveCleanManualTaskIdentityMatrix(AeiObjects aeiObjects) {
		aeiObjects.getWork().setManualTaskIdentityMatrix(new ManualTaskIdentityMatrix());
	}

	private void arriveCleanManualEmpowerMap(AeiObjects aeiObjects) {
		aeiObjects.getWork().setManualEmpowerMap(new LinkedHashMap<>());
	}

	private void arriveUpdateWorkThroughManual(AeiObjects aeiObjects) throws Exception {
		boolean value = aeiObjects.getWorkLogs().stream()
				.filter(o -> Objects.equals(ActivityType.manual, o.getArrivedActivityType())
						&& BooleanUtils.isTrue(o.getConnected()))
				.count() > 0;
		aeiObjects.getWork().setWorkThroughManual(value);
	}

	private void callBeforeArriveScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallBeforeArriveScript())
				&& this.hasBeforeArriveScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			CompiledScript cs = null;
			if (this.hasBeforeArriveScript(aeiObjects.getProcess())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_BEFOREARRIVE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			if (this.hasBeforeArriveScript(aeiObjects.getActivity())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_BEFOREARRIVE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
		}
	}

	private boolean callAfterArriveScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallAfterArriveScript())
				&& this.hasAfterArriveScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			CompiledScript cs = null;
			if (this.hasAfterArriveScript(aeiObjects.getProcess())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_AFTERARRIVE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			if (this.hasAfterArriveScript(aeiObjects.getActivity())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_AFTERARRIVE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			return true;
		}
		return false;
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
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingAttributes);
			aeiObjects.getUpdateWorks().add(work);
			// 如果是调度路由,需要重新设置froceRoute
			if (BooleanUtils.isNotTrue(work.getBeforeExecuted())) {
				// 仅执行一次BeforeExecuteScript中的代码
				this.callBeforeExecuteScript(aeiObjects);
				work.setBeforeExecuted(true);
			}
			List<Work> works = new ArrayList<>();
			// 8.2版本以前没有使用destinationActivity作为强制路由,如果这里不单独判断,老版本的数据会原地转圈,在同一环节再次进入,重新生成activityToken,现象就是所有待办会重新生成.
			if (StringUtils.isNotEmpty(work.getDestinationActivity())
					&& Objects.nonNull(work.getDestinationActivityType())
					&& BooleanUtils.isTrue(aeiObjects.getWork().getForceRouteEnable())) {
				works.add(work);
			} else {
				// 运行业务方法
				works.addAll(this.executeProcessing(aeiObjects));
			}
			if (ListTools.isNotEmpty(works)) {
				for (Work o : works) {
					results.add(o.getId());
				}
			}
			aeiObjects.commit();
			// 发送在队列中的待办消息, 待办消息必须在数据提交后发送,否则会不到待办
			this.executeCommitted(aeiObjects, works);
			if (ListTools.isNotEmpty(works) && callAfterExecuteScript(aeiObjects) && aeiObjects.commitData()) {
				// 已经有返回的work将要离开当前环节,执行AfterExecuteScript中的代码可能修改了data数据.
				aeiObjects.entityManagerContainer().commit();
			}
			if (StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScript())
					|| StringUtils.isNotEmpty(aeiObjects.getProcess().getAfterEndScriptText())) {
				CompiledScript cs = aeiObjects.business().element().getCompiledScript(
						aeiObjects.getWork().getApplication(), aeiObjects.getProcess(), Business.EVENT_PROCESSAFTEREND);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return results;
	}

	private void callBeforeExecuteScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallBeforeExecuteScript())
				&& this.hasBeforeExecuteScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			ScriptContext scriptContext = aeiObjects.scriptContext();
			CompiledScript cs = null;
			if (this.hasBeforeExecuteScript(aeiObjects.getProcess())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_BEFOREEXECUTE);
			}
			if (this.hasBeforeExecuteScript(aeiObjects.getActivity())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_BEFOREEXECUTE);
			}
			JsonScriptingExecutor.eval(cs, scriptContext);
		}
	}

	private boolean callAfterExecuteScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallAfterExecuteScript())
				&& this.hasAfterExecuteScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			CompiledScript cs = null;
			if (this.hasAfterExecuteScript(aeiObjects.getProcess())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_AFTEREXECUTE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			if (this.hasAfterExecuteScript(aeiObjects.getActivity())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_AFTEREXECUTE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			return true;
		}
		return false;
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
			AeiObjects aeiObjects = new AeiObjects(this.business(), work, activity, processingAttributes);
			aeiObjects.getUpdateWorks().add(work);
			// 运行查询路由前脚本
			this.callBeforeInquireScript(aeiObjects);
			// 运行主方法
			Route selectRoute = this.inquireProcessing(aeiObjects);
			// 主方法运行完成
			if (null == selectRoute) {
				return results;
			}
			List<Work> works = new ArrayList<>();
			// 运行查询路由后脚本
			work.setDestinationActivity(selectRoute.getActivity());
			work.setDestinationActivityType(selectRoute.getActivityType());
			work.setDestinationRoute(selectRoute.getId());
			work.setDestinationRouteName(selectRoute.getName());
			works.add(work);
			for (Work o : works) {
				results.add(o.getId());
			}
			if ((null != aeiObjects.getProcess())
					&& StringUtils.equalsIgnoreCase(aeiObjects.getProcess().getSerialActivity(),
							aeiObjects.getActivity().getId())
					&& (StringUtils.equals(aeiObjects.getProcess().getSerialPhase(), Process.SERIALPHASE_INQUIRE))
					&& StringUtils.isEmpty(work.getSerial())) {
				SerialBuilder serialBuilder = new SerialBuilder(ThisApplication.context(),
						this.entityManagerContainer(), work.getProcess(), work.getId());
				String serial = serialBuilder.concrete(aeiObjects);
				work.setSerial(serial);
			}
			aeiObjects.commit();
			this.inquireCommitted(aeiObjects);
			// 运行 AfterInquireScript事件
			if (this.callAfterInquireScript(aeiObjects) && aeiObjects.commitData()) {
				// 执行AfterInquireScript中的代码可能修改了data数据.
				aeiObjects.entityManagerContainer().commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return results;
	}

	private void callBeforeInquireScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallBeforeInquireScript())
				&& this.hasBeforeInquireScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			ScriptContext scriptContext = aeiObjects.scriptContext();
			CompiledScript cs = null;
			if (this.hasBeforeInquireScript(aeiObjects.getProcess())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_BEFOREINQUIRE);
			}
			if (this.hasBeforeInquireScript(aeiObjects.getActivity())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_BEFOREINQUIRE);
			}
			JsonScriptingExecutor.eval(cs, scriptContext);
		}
	}

	private boolean callAfterInquireScript(AeiObjects aeiObjects) throws Exception {
		if (BooleanUtils.isTrue(aeiObjects.getActivityProcessingConfigurator().getCallAfterInquireScript())
				&& this.hasAfterInquireScript(aeiObjects.getProcess(), aeiObjects.getActivity())) {
			CompiledScript cs = null;
			if (this.hasAfterInquireScript(aeiObjects.getProcess())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getProcess(), Business.EVENT_AFTERINQUIRE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			if (this.hasAfterInquireScript(aeiObjects.getActivity())) {
				cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
						aeiObjects.getActivity(), Business.EVENT_AFTERINQUIRE);
				JsonScriptingExecutor.eval(cs, aeiObjects.scriptContext());
			}
			return true;
		}
		return false;
	}

	protected abstract Work arriveProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void arriveCommitted(AeiObjects aeiObjects) throws Exception;

	protected abstract List<Work> executeProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void executeCommitted(AeiObjects aeiObjects, List<Work> works) throws Exception;

	protected abstract Route inquireProcessing(AeiObjects aeiObjects) throws Exception;

	protected abstract void inquireCommitted(AeiObjects aeiObjects) throws Exception;

}