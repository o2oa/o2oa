package com.x.processplatform.assemble.surface.jaxrs.task;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.logger.Audit;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.ProcessingType;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.FormField_;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

class ActionProcessingNeural extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionProcessingNeural.class);

	private static DataItemConverter<Item> itemConverter = new DataItemConverter<>(Item.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			logger.debug(effectivePerson, "receive:" + wi);
			Business business = new Business(emc);
			emc.beginTransaction(Task.class);
			Task task = emc.find(id, Task.class);
			if (null == task) {
				throw new ExceptionEntityNotExist(id, Task.class);
			}
			if (!StringUtils.equalsIgnoreCase(task.getPerson(), effectivePerson.getDistinguishedName())) {
				throw new ExceptionAccessDenied(effectivePerson, task);
			}
			Process process = business.process().pick(task.getProcess());
			if (null == process) {
				throw new ExceptionEntityNotExist(task.getProcess(), Process.class);
			}
			String routeName = this.neural(business, process, task);
			emc.commit();
			/* processing task */
			Map<String, Object> requestAttributes = new HashMap<String, Object>();
			ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
					"task/" + URLEncoder.encode(task.getId(), DefaultCharset.name) + "/processing", requestAttributes);
			/** 流程处理完毕,开始组装返回信息 */
			Wo wo = new Wo();
			wo.setWorkLogList(this.referenceWorkLog(business, task));
			wo.setRouteName(routeName);
			result.setData(wo);
			audit.log(null, "审批");
			return result;
		}
	}

	private List<WoWorkLog> referenceWorkLog(Business business, Task task) throws Exception {
		List<WoWorkLog> os = WoWorkLog.copier.copy(business.entityManagerContainer().list(WorkLog.class,
				business.workLog().listWithFromActivityTokenForwardNotConnected(task.getActivityToken())));
		List<WoTaskCompleted> _taskCompleteds = WoTaskCompleted.copier
				.copy(business.taskCompleted().listWithJobObject(task.getJob()));
		List<WoTask> _tasks = WoTask.copier.copy(business.task().listWithJobObject(task.getJob()));
		os = business.workLog().sort(os);

		Map<String, List<WoTaskCompleted>> _map_taskCompleteds = _taskCompleteds.stream()
				.collect(Collectors.groupingBy(o -> o.getActivityToken()));

		Map<String, List<WoTask>> _map_tasks = _tasks.stream()
				.collect(Collectors.groupingBy(o -> o.getActivityToken()));

		for (WoWorkLog o : os) {
			List<WoTaskCompleted> _parts_taskCompleted = _map_taskCompleteds.get(o.getFromActivityToken());
			o.setTaskCompletedList(new ArrayList<WoTaskCompleted>());
			if (!ListTools.isEmpty(_parts_taskCompleted)) {
				for (WoTaskCompleted _taskCompleted : business.taskCompleted().sort(_parts_taskCompleted)) {
					o.getTaskCompletedList().add(_taskCompleted);
					if (_taskCompleted.getProcessingType().equals(ProcessingType.retract)) {
						TaskCompleted _retract = new TaskCompleted();
						o.copyTo(_retract);
						_retract.setRouteName("撤回");
						_retract.setOpinion("撤回");
						_retract.setStartTime(_retract.getRetractTime());
						_retract.setCompletedTime(_retract.getRetractTime());
						o.getTaskCompletedList().add(WoTaskCompleted.copier.copy(_retract));
					}
				}
			}
			List<WoTask> _parts_tasks = _map_tasks.get(o.getFromActivityToken());
			o.setTaskList(new ArrayList<WoTask>());
			if (!ListTools.isEmpty(_parts_tasks)) {
				o.setTaskList(business.task().sort(_parts_tasks));
			}
		}
		return os;
	}

	private String neural(Business business, Process process, Task task) throws Exception {
		if (task.getRouteNameList().size() == 1) {
			/** 如果只有一条路由就不用判断了 */
			task.setRouteName(task.getRouteNameList().get(0));
			return task.getRouteName();
		}
		List<String> fields = this.listNumberFormField(business, process);
		if (fields.isEmpty()) {
			/** 如果没有可以用于分析的数据,那么返回第一条路由 */
			task.setRouteName(task.getRouteNameList().get(0));
			return task.getRouteName();
		} else {
			DataSet trainingSet = new DataSet(fields.size(), task.getRouteNameList().size());
			List<TaskCompleted> taskCompleteds = listTaskCompleted(business, task.getActivity(), task.getPerson());
			for (TaskCompleted o : taskCompleteds) {
				if (ListTools.contains(task.getRouteNameList(), o.getRouteName())) {
					/** 包含有需要的决策 */
					Data data = this.loadData(business, o.getJob());
					double[] input = new double[fields.size()];
					for (int i = 0; i < fields.size(); i++) {
						Number n = data.find(fields.get(i), Number.class, 0);
						input[i] = n.doubleValue();
					}
					double[] desiredOutput = new double[task.getRouteNameList().size()];
					Arrays.fill(desiredOutput, 0);
					int index = task.getRouteNameList().indexOf(o.getRouteName());
					if (index > -1) {
						desiredOutput[index] = 1.0;
					}
					DataSetRow row = new DataSetRow();
					row.setInput(input);
					row.setDesiredOutput(desiredOutput);
					trainingSet.add(row);
				}
			}
			if (trainingSet.isEmpty()) {
				throw new ExceptionEmptyTrainingSet(task.getTitle(), task.getId(), task.getPerson());
			}
			MultiLayerPerceptron perceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, fields.size(),
					fields.size() * 2, task.getRouteNameList().size());
			perceptron.getLearningRule().setMaxIterations(100);
			perceptron.learn(trainingSet);
			/** 输入当前值 */
			Data data = this.loadData(business, task.getJob());
			double[] input = new double[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				Number n = data.find(fields.get(i), Number.class, 0);
				input[i] = n.doubleValue();
			}
			perceptron.setInput(input);
			perceptron.calculate();
			double[] networkOutput = perceptron.getOutput();
			networkOutput = this.competition(networkOutput);
			int index = ArrayUtils.indexOf(networkOutput, 1.0);
			if (index < 0) {
				index = 0;
			}
			/* 重新设置路由 */
			task.setRouteName(task.getRouteNameList().get(index));
			return task.getRouteName();
		}
	}

	private Data loadData(Business business, String job) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), job);
		List<Item> list = em.createQuery(cq.where(p)).getResultList();
		if (list.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = itemConverter.assemble(list);
			if (jsonElement.isJsonObject()) {
				return gson.fromJson(jsonElement, Data.class);
			} else {
				return new Data();
			}
		}
	}

	private List<String> listNumberFormField(Business business, Process process) throws Exception {
		List<String> formIds = this.listForm(business, process);
		List<String> fields = this.listNumberFormField(business, formIds);
		return fields;
	}

	private List<String> listNumberFormField(Business business, List<String> formIds) throws Exception {
		EntityManager em = business.entityManagerContainer().get(FormField.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormField> root = cq.from(FormField.class);
		Predicate p = root.get(FormField_.form).in(formIds);
		p = cb.and(p, cb.equal(root.get(FormField_.dataType), "number"));
		cq.select(root.get(FormField_.name)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	private List<String> listForm(Business business, Process process) throws Exception {
		List<String> ids = new ArrayList<>();
		ids.add(business.begin().getWithProcess(process).getForm());
		business.agent().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.cancel().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.choice().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.delay().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.embed().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.end().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.invoke().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.manual().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.merge().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.message().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.parallel().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.service().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		business.split().listWithProcess(process).forEach(o -> {
			ids.add(o.getForm());
		});
		return ListTools.trim(ids, true, true);
	}

	private List<TaskCompleted> listTaskCompleted(Business business, String activity, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(TaskCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TaskCompleted> cq = cb.createQuery(TaskCompleted.class);
		Root<TaskCompleted> root = cq.from(TaskCompleted.class);
		Predicate p = cb.equal(root.get(TaskCompleted_.activity), activity);
		p = cb.and(p, cb.equal(root.get(TaskCompleted_.person), person));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	private double[] competition(double[] d) {
		double[] output = d;
		double[] re = new double[output.length];
		int maxIndex = 0;
		double maxValue = Double.MIN_VALUE;
		for (int i = 0; i < output.length; i++) {
			if (output[i] > maxValue) {
				maxIndex = i;
				maxValue = output[i];
			}
		}
		for (int i = 0; i < re.length; i++) {
			if (i == maxIndex) {
				re[i] = 1;
			} else {
				re[i] = 0;
			}
		}
		return re;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("决策")
		private String routeName;

		@FieldDescribe("返回工作记录")
		List<WoWorkLog> workLogList = new ArrayList<>();

		public String getRouteName() {
			return routeName;
		}

		public void setRouteName(String routeName) {
			this.routeName = routeName;
		}

		public List<WoWorkLog> getWorkLogList() {
			return workLogList;
		}

		public void setWorkLogList(List<WoWorkLog> workLogList) {
			this.workLogList = workLogList;
		}

	}

	public static class WoWorkLog extends WorkLog {

		private static final long serialVersionUID = 1307569946729101786L;

		static WrapCopier<WorkLog, WoWorkLog> copier = WrapCopierFactory.wo(WorkLog.class, WoWorkLog.class, null,
				JpaObject.FieldsInvisible);

		private List<WoTaskCompleted> taskCompletedList;

		private List<WoTask> taskList;

		public List<WoTaskCompleted> getTaskCompletedList() {
			return taskCompletedList;
		}

		public void setTaskCompletedList(List<WoTaskCompleted> taskCompletedList) {
			this.taskCompletedList = taskCompletedList;
		}

		public List<WoTask> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<WoTask> taskList) {
			this.taskList = taskList;
		}

	}

	public static class WoTaskCompleted extends TaskCompleted {

		private static final long serialVersionUID = -7253999118308715077L;

		static WrapCopier<TaskCompleted, WoTaskCompleted> copier = WrapCopierFactory.wo(TaskCompleted.class,
				WoTaskCompleted.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoTask extends Task {

		private static final long serialVersionUID = 2702712453822143654L;

		static WrapCopier<Task, WoTask> copier = WrapCopierFactory.wo(Task.class, WoTask.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("神经网络类型")
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

}