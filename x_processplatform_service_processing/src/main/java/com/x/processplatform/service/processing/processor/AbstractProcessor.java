package com.x.processplatform.service.processing.processor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapCompanyDuty;
import com.x.organization.core.express.wrap.WrapDepartmentDuty;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.tools.DataHelper;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.serial.builder.SerialBuilder;
import com.x.processplatform.service.processing.BindingPair;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
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
			DataHelper dataHelper = new DataHelper(this.entityManagerContainer(), work);
			Data data = dataHelper.get();
			ActivityProcessingConfigurator activityConfigurator = processingConfigurator.get(activityType);
			this.callBeforeArriveScript(activityConfigurator, attributes, activity, work, data);
			this.arriveActivity(activityConfigurator, work, activity);
			// this.createRead(activityConfigurator, attributes, activity, work,
			// data);
			// this.createReview(activityConfigurator, attributes, activity,
			// work, data);
			work = this.arriveProcessing(processingConfigurator, attributes, work, data, activity);
			if (null == work) {
				throw new Exception("arrvie return empty, work{id:" + workId + "}.");
			}
			if (null != process) {
				if (StringUtils.equalsIgnoreCase(process.getSerialActivity(), activity.getId())) {
					if (StringUtils.isEmpty(work.getSerial())) {
						SerialBuilder serialBuilder = new SerialBuilder(this.entityManagerContainer(),
								work.getProcess(), work.getId());
						work.setSerial(serialBuilder.concrete());
					}
				}
			}
			callAfterArriveScript(activityConfigurator, attributes, activity, work, data);
			dataHelper.update(data);
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
			DataHelper dataHelper = new DataHelper(this.entityManagerContainer(), work);
			Data data = dataHelper.get();
			/**
			 * 运行执行前脚本<br>
			 * manul环节单独判断 如果没有运行过beforeArrivedExecuteScript那么运行脚本
			 */
			this.callBeforeArrivedExecuteScript(activityConfigurator, attributes, activity, work, data);
			this.callBeforeExecuteScript(activityConfigurator, attributes, activity, work, data);
			List<Work> works = this.executeProcessing(processingConfigurator, attributes, work, data, activity);
			/**
			 * manul环节单独判断<br>
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
				dataHelper.update(data);
			}
			this.entityManagerContainer().commit();
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
			System.out.println(this.getClass().getSimpleName() + " inquiry workd:" + workId + ", activityName:"
					+ activity.getName());
			if (BooleanUtils.isTrue(work.getInquired())) {
				results.add(work.getId());
				return results;
			}
			/* 需要运行 */
			this.entityManagerContainer().beginTransaction(Work.class);

			ActivityProcessingConfigurator activityConfigurator = processingConfigurator.get(activityType);
			List<Route> routes = this.business().element().listRouteWithActvity(work.getActivity(), activityType);
			DataHelper dataHelper = new DataHelper(this.entityManagerContainer(), work);
			Data data = dataHelper.get();
			/* 运行查询路由前脚本 */
			this.callBeforeInquireScript(activityConfigurator, attributes, activity, routes, work, data);
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
			dataHelper.update(data);
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

	/* 计算节点中所有的Review，全部翻译成Identity */
	protected List<String> translateReviewIdentity(ProcessingAttributes attributes, Work work, Data data,
			Activity activity) throws Exception {
		List<String> identities = SetUniqueList.setUniqueList(new ArrayList<String>());
		Organization organization = new Organization();
		for (String str : activity.getReviewIdentityList()) {
			if (StringUtils.isNotEmpty(str)) {
				identities.add(str);
			}
		}
		for (String str : activity.getReviewDepartmentList()) {
			if (StringUtils.isNotEmpty(str)) {
				for (WrapIdentity o : organization.identity().listWithDepartmentSubDirect(str)) {
					identities.add(o.getName());
				}
			}
		}
		if ((StringUtils.isNotEmpty(activity.getReviewScript()))
				|| (StringUtils.isNotEmpty(activity.getReviewScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, activity);
			identities.addAll(scriptHelper.evalAsStringList(work.getApplication(), activity.getReviewScript(),
					activity.getReviewScriptText()));
		}
		return this.checkIdentity(identities);
	}

	/* 计算节点中所有的Read，全部翻译成Identity */
	protected List<String> translateReadIdentity(ProcessingAttributes attributes, Work work, Data data,
			Activity activity) throws Exception {
		List<String> identities = SetUniqueList.setUniqueList(new ArrayList<String>());
		/* 指定待阅人 */
		for (String str : activity.getReadIdentityList()) {
			if (StringUtils.isNotEmpty(str)) {
				identities.add(str);
			}
		}
		/* 指定待阅部门 */
		for (String str : activity.getReadDepartmentList()) {
			if (StringUtils.isNotEmpty(str)) {
				for (WrapIdentity o : this.business().organization().identity().listWithDepartmentSubDirect(str)) {
					identities.add(o.getName());
				}
			}
		}
		/* 使用待阅脚本 */
		if ((StringUtils.isNotEmpty(activity.getReadScript()))
				|| (StringUtils.isNotEmpty(activity.getReadScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, activity);
			identities.addAll(scriptHelper.evalAsStringList(work.getApplication(), activity.getReadScript(),
					activity.getReadScriptText()));
		}
		/* 选择了Read角色 */
		if (StringUtils.isNotEmpty(activity.getReadDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(activity.getReadDuty(), JsonArray.class);
			Iterator<JsonElement> iterator = array.iterator();
			while (iterator.hasNext()) {
				JsonObject o = iterator.next().getAsJsonObject();
				String name = o.get("name").getAsString();
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data,
						activity);
				String str = scriptHelper.evalAsString(work.getApplication(), null, o.get("code").getAsString());
				if (StringUtils.isNotEmpty(str)) {
					/* 先尝试去取公司职务 */
					WrapCompanyDuty wrapCompanyDuty = this.business().organization().companyDuty().getWithName(name,
							str);
					if (null != wrapCompanyDuty) {
						identities.addAll(wrapCompanyDuty.getIdentityList());
					} else {
						/* 再尝试取部门职务 */
						WrapDepartmentDuty wrapDepartmentDuty = this.business().organization().departmentDuty()
								.getWithName(name, str);
						if (null != wrapDepartmentDuty) {
							identities.addAll(wrapDepartmentDuty.getIdentityList());
						}
					}
				}
			}
		}
		return this.checkIdentity(identities);
	}

	protected List<String> checkIdentity(List<String> identities) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : identities) {
			WrapIdentity o = this.business().organization().identity().getWithName(str);
			if ((null != o) && (StringUtils.isNotEmpty(o.getName()))) {
				list.add(o.getName());
			}
		}
		return list;
	}
}