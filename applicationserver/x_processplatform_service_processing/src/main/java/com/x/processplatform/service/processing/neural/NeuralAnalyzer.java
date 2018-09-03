package com.x.processplatform.service.processing.neural;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;

import com.google.gson.JsonElement;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.TaskCompleted_;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.FormField_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Business;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class NeuralAnalyzer {

	private DataItemConverter<Item> itemConverter = new DataItemConverter<>(Item.class);

	public Route analysis(Business business, Task task) throws Exception {
		List<Route> routes = business.element().listRouteWithManual(task.getActivity());
		if (routes.size() == 0) {
			return null;
		}
		if (routes.size() == 1) {
			/** 如果只有一条路由就不用判断了 */
			return routes.get(0);
		}
		Process process = business.element().get(task.getProcess(), Process.class);
		List<TaskCompleted> taskCompleteds = listTaskCompleted(business, task.getActivity(), task.getPerson());
		if (null == process || ListTools.isEmpty(taskCompleteds)) {
			/** 如果流程不存在或者已办为空直接返回 */
			return routes.get(0);
		}
		List<String> fields = this.listNumberFormField(business, process);
		if (fields.isEmpty()) {
			String value = this.maxFromTaskCompleted(taskCompleteds);
			if (task.getRouteNameList().contains(value)) {
				for (Route route : routes) {
					if (StringUtils.equals(route.getName(), value)) {
						return route;
					}
				}
			}
			/** 如果没有可以用于分析的数据,那么返回第一条路由 */
			return routes.get(0);
		}
		List<String> routeNames = ListTools.extractField(routes, Route.name_FIELDNAME, String.class, false, false);
		DataSet trainingSet = new DataSet(fields.size(), routeNames.size());
		for (TaskCompleted o : taskCompleteds) {
			if (ListTools.contains(routeNames, o.getRouteName())) {
				/** 包含有需要的决策 */
				Data data = this.load_data(business, o.getJob());
				double[] input = new double[fields.size()];
				for (int i = 0; i < fields.size(); i++) {
					Number n = data.find(fields.get(i), Number.class, 0);
					input[i] = n.doubleValue();
				}
				double[] desiredOutput = new double[routeNames.size()];
				Arrays.fill(desiredOutput, 0);
				int index = routeNames.indexOf(o.getRouteName());
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
			/* 没有产生可用的训练集 */
			return routes.get(0);
		}
		MultiLayerPerceptron perceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, fields.size(),
				fields.size() * 2, routeNames.size());
		perceptron.getLearningRule().setMaxIterations(100);
		perceptron.learn(trainingSet);
		/* 输入当前值 */
		Data data = this.load_data(business, task.getJob());
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
		Route selected = ListTools.parallel(routeNames, routeNames.get(index), routes);
		return selected;
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

	private List<String> listNumberFormField(Business business, Process process) throws Exception {
		List<String> formIds = business.element().listFormWithProcess(process);
		EntityManager em = business.entityManagerContainer().get(FormField.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FormField> root = cq.from(FormField.class);
		Predicate p = root.get(FormField_.form).in(formIds);
		p = cb.and(p, cb.equal(root.get(FormField_.dataType), "number"));
		cq.select(root.get(FormField_.name)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
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

	private Data load_data(Business business, String job) throws Exception {
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
				return XGsonBuilder.instance().fromJson(jsonElement, Data.class);
			} else {
				return new Data();
			}
		}
	}

	private String maxFromTaskCompleted(List<TaskCompleted> list) throws Exception {
		List<String> values = ListTools.extractField(list, "routeName", String.class, true, true);
		return ListTools.maxCountElement(values);
	}

}
