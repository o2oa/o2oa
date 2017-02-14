package com.x.processplatform.service.processing.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapCompanyDuty;
import com.x.organization.core.express.wrap.WrapDepartmentDuty;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ScriptHelper;
import com.x.processplatform.service.processing.ScriptHelperFactory;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;

public class ManualProcessor extends AbstractProcessor {

	public ManualProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		Manual manual = (Manual) activity;
		work.setManualTaskIdentityList(this.translateTaskIdentity(attributes, work, data, manual));
		return work;
	}

	@Override
	protected List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity) throws Exception {
		List<Work> results = new ArrayList<>();
		Manual manual = (Manual) activity;
		boolean passThrough = false;
		this.entityManagerContainer().beginTransaction(Task.class);
		switch (manual.getManualMode()) {
		case single:
			passThrough = this.single(manual, work, data, attributes);
			break;
		case parallel:
			passThrough = this.parallel(manual, work, data, attributes);
			break;
		case queue:
			passThrough = this.queue(manual, work, data, attributes);
			break;
		default:
			throw new Exception("unknown manualMode:" + manual.getManualMode());
		}
		if (passThrough) {
			/* 清除可能是多余的待办 */
			this.entityManagerContainer().delete(Task.class, this.business().task().listWithWork(work.getId()));
			results.add(work);
		}
		return results;
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		List<Route> results = new ArrayList<>();
		/* 仅有单条路由 */
		if (routes.size() == 1) {
			results.add(routes.get(0));
		} else if (routes.size() > 1) {
			/* 存在多条路由 */
			List<TaskCompleted> taskCompletedList = this.listEffectiveTaskCompleted(work);
			String name = this.choiceRouteName(taskCompletedList);
			System.out.println("路由名称:" + name);
			for (Route o : routes) {
				if (o.getName().equalsIgnoreCase(name)) {
					results.add(o);
					break;
				}
			}
		}
		return results;
	}

	/* 通过已办存根选择某条路由 */
	private String choiceRouteName(List<TaskCompleted> list) throws Exception {
		String result = "";
		Map<String, Integer> map = new HashMap<>();
		for (TaskCompleted o : list) {
			if ((!o.getProcessingType().equals(ProcessingType.reset))
					&& (!o.getProcessingType().equals(ProcessingType.retract))) {
				/* 跳过重置处理人的路由 */
				String name = StringUtils.trimToNull(o.getRouteName());
				if (StringUtils.isNotEmpty(name)) {
					if (null == map.get(name)) {
						map.put(name, 1);
					} else {
						map.put(name, map.get(name) + 1);
					}
				}
			}
		}
		Integer count = 0;
		for (Entry<String, Integer> en : map.entrySet()) {
			if (en.getValue() > count) {
				result = en.getKey();
				count = en.getValue();
			}
		}
		if (StringUtils.isEmpty(result)) {
			throw new Exception("can not choice routeName.");
		}
		return result;
	}

	private boolean single(Manual manual, Work work, Data data, ProcessingAttributes attributes) throws Exception {
		boolean passThrough = false;
		/* 找到所有的已办 */
		List<TaskCompleted> list = listEffectiveTaskCompleted(work);
		if (!list.isEmpty()) {
			/* 所有预计的处理人中已经有已办,这个环节已经产生了已办，可以离开换个环节。 */
			passThrough = true;
		} else {
			passThrough = false;
			/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
			List<String> expected = this.checkIdentity(new ArrayList<>(work.getManualTaskIdentityList()));
			if (expected.isEmpty() || StringUtils.isBlank(StringUtils.join(expected, ""))) {
				throw new Exception("expected is empty.");
			}
			List<Task> existed = this.entityManagerContainer().fetchAttribute(
					this.business().task().listWithActivityToken(work.getActivityToken()), Task.class, "identity");
			for (Task o : existed) {
				if (!expected.remove(o.getIdentity())) {
					/* 删除多余待办 */
					this.entityManagerContainer().delete(Task.class, o.getId());
				}
			}
			/* 这里剩余的应该是没有生成待办的人员 */
			if (!expected.isEmpty()) {
				/* 可选择路由的名称列表 */
				List<String> routeList = new ArrayList<>();
				List<String> routeNameList = new ArrayList<>();
				for (Route o : this.business().element().listRouteWithManual(manual.getId())) {
					routeList.add(o.getId());
					routeNameList.add(o.getName());
				}
				for (String str : expected) {
					Task task = this.createTask(this.business(), manual, work, attributes, data, str);
					task.setRouteList(routeList);
					task.setRouteNameList(routeNameList);
					this.entityManagerContainer().persist(task, CheckPersistType.all);
					/* 创建提醒 */
					this.sendTaskMessage(task);
				}
			}
		}
		return passThrough;
	}

	private boolean parallel(Manual manual, Work work, Data data, ProcessingAttributes attributes) throws Exception {
		boolean passThrough = false;
		/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
		List<String> expected = this.checkIdentity(new ArrayList<>(work.getManualTaskIdentityList()));
		if (expected.isEmpty() || StringUtils.isBlank(StringUtils.join(expected, ""))) {
			throw new Exception("expected is empty.");
		}
		/* 取得本环节已经处理的已办 */
		List<TaskCompleted> done = this.listEffectiveTaskCompleted(work);
		/* 将已经处理的人从期望值中移除 */
		for (TaskCompleted o : done) {
			expected.remove(o.getIdentity());
		}
		if (expected.isEmpty()) {
			/* 所有人已经处理完成。 */
			passThrough = true;
		} else {
			passThrough = false;
			/* 还有人没有处理，开始判断待办,取到本环节的所有待办 */
			List<Task> existed = this.entityManagerContainer().fetchAttribute(
					this.business().task().listWithActivityToken(work.getActivityToken()), Task.class, "identity");
			for (Task o : existed) {
				if (!expected.remove(o.getIdentity())) {
					/* 删除多余待办 */
					this.entityManagerContainer().delete(Task.class, o.getId());
				}
			}
			/* 这里剩余的应该是没有生成待办的人员 */
			if (!expected.isEmpty()) {
				/* 可选择路由的名称列表 */
				List<String> routeList = new ArrayList<>();
				List<String> routeNameList = new ArrayList<>();
				for (Route o : this.business().element().listRouteWithManual(manual.getId())) {
					routeList.add(o.getId());
					routeNameList.add(o.getName());
				}
				for (String str : expected) {
					Task task = this.createTask(this.business(), manual, work, attributes, data, str);
					task.setRouteList(routeList);
					task.setRouteNameList(routeNameList);
					this.entityManagerContainer().persist(task, CheckPersistType.all);
					/* 创建提醒 */
					this.sendTaskMessage(task);
				}
			}
		}
		return passThrough;
	}

	private boolean queue(Manual manual, Work work, Data data, ProcessingAttributes attributes) throws Exception {
		boolean passThrough = false;
		/* 取到期望的待办人员，由于要进行处理需要转换成可读写List */
		List<String> expected = this.checkIdentity(new ArrayList<>(work.getManualTaskIdentityList()));
		if (expected.isEmpty() || StringUtils.isBlank(StringUtils.join(expected, ""))) {
			throw new Exception("expected is empty.");
		}
		List<TaskCompleted> done = this.listEffectiveTaskCompleted(work);
		/* 将已经处理的人从期望值中移除 */
		for (TaskCompleted o : done) {
			expected.remove(o.getIdentity());
		}
		if (expected.isEmpty()) {
			/* 所有人已经处理完成。 */
			passThrough = true;
		} else {
			passThrough = false;
			String next = expected.get(0);
			/* 还有人没有处理，开始判断待办,取到本环节的所有待办,理论上只能有一条待办 */
			List<Task> existed = this.entityManagerContainer().fetchAttribute(
					this.business().task().listWithActivityToken(work.getActivityToken()), Task.class, "identity");
			/* 理论上只能有一条待办 */
			boolean find = false;
			for (Task o : existed) {
				if (!StringUtils.equals(o.getIdentity(), next)) {
					this.entityManagerContainer().delete(Task.class, o.getId());
				} else {
					find = true;
				}
			}
			/* 当前处理人没有待办 */
			if (!find) {
				/* 可选择路由的名称列表 */
				List<String> routeList = new ArrayList<>();
				List<String> routeNameList = new ArrayList<>();
				for (Route o : this.business().element().listRouteWithManual(manual.getId())) {
					routeList.add(o.getId());
					routeNameList.add(o.getName());
				}
				Task task = this.createTask(this.business(), manual, work, attributes, data, next);
				task.setRouteList(routeList);
				task.setRouteNameList(routeNameList);
				this.entityManagerContainer().persist(task, CheckPersistType.all);
				/* 创建提醒 */
				this.sendTaskMessage(task);
			}
		}
		return passThrough;
	}

	/* 计算manual节点中所有的待办，全部翻译成Identity */
	private List<String> translateTaskIdentity(ProcessingAttributes attributes, Work work, Data data, Manual manual)
			throws Exception {
		List<String> identities = SetUniqueList.setUniqueList(new ArrayList<String>());
		/* 指定的身份 */
		for (String str : manual.getTaskIdentityList()) {
			if (StringUtils.isNotEmpty(str)) {
				identities.add(str);
			}
		}
		/* 指定处理部门 */
		for (String str : manual.getTaskDepartmentList()) {
			if (StringUtils.isNotEmpty(str)) {
				for (WrapIdentity o : this.business().organization().identity().listWithDepartmentSubDirect(str)) {
					identities.add(o.getName());
				}
			}
		}
		/* 使用脚本计算 */
		if ((StringUtils.isNotEmpty(manual.getTaskScript())) || (StringUtils.isNotEmpty(manual.getTaskScriptText()))) {
			ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, manual);
			List<String> list = scriptHelper.evalAsStringList(work.getApplication(), manual.getTaskScript(),
					manual.getTaskScriptText());
			for (String str : list) {
				if (StringUtils.isNotEmpty(str)) {
					identities.add(str);
				}
			}
		}
		/* 选择了Task角色 */
		if (StringUtils.isNotEmpty(manual.getTaskDuty())) {
			JsonArray array = XGsonBuilder.instance().fromJson(manual.getTaskDuty(), JsonArray.class);
			Iterator<JsonElement> iterator = array.iterator();
			while (iterator.hasNext()) {
				JsonObject o = iterator.next().getAsJsonObject();
				String name = o.get("name").getAsString();
				ScriptHelper scriptHelper = ScriptHelperFactory.create(this.business(), attributes, work, data, manual);
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
		if (ListTools.isNotEmpty(identities)) {
			identities = this.checkIdentity(identities);
		}
		if (identities.isEmpty()) {
			/* 如果活动没有找到任何可用的处理人,那么强制设置处理人为文档创建者 */
			this.business().work().addHint(work, "没有找到任何可用的处理人,强制设置处理人为创建者.");
			identities.add(work.getCreatorIdentity());
		}
		return identities;
	}

	/* 所有有效的已办 */
	private List<TaskCompleted> listEffectiveTaskCompleted(Work work) throws Exception {
		List<String> ids = this.business().taskCompleted().listWithActivityTokenInIdentityList(work.getActivityToken(),
				work.getManualTaskIdentityList());
		List<TaskCompleted> list = new ArrayList<>();
		for (TaskCompleted o : this.business().entityManagerContainer().list(TaskCompleted.class, ids)) {
			if ((!o.getProcessingType().equals(ProcessingType.retract))
					&& (!o.getProcessingType().equals(ProcessingType.retract))) {
				list.add(o);
			}
		}
		return list;
	}
}