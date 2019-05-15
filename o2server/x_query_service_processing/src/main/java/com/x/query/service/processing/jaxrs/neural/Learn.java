/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2.js                                                 |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/
package com.x.query.service.processing.jaxrs.neural;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.eval.ClassifierEvaluator;
import org.neuroph.eval.Evaluation;
import org.neuroph.eval.EvaluationResult;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.NeuralNetworkCODEC;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ByteTools;
import com.x.base.core.project.tools.DoubleTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.MapTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.query.core.entity.neural.Entry;
import com.x.query.core.entity.neural.InText;
import com.x.query.core.entity.neural.InText_;
import com.x.query.core.entity.neural.InValue;
import com.x.query.core.entity.neural.OutText;
import com.x.query.core.entity.neural.OutValue;
import com.x.query.core.entity.neural.Model;
import com.x.query.service.processing.Business;

public class Learn {

	private static Logger logger = LoggerFactory.getLogger(Learn.class);

	private volatile static String learningModel = "";

	private volatile static boolean stop = false;

	private Learn() {

	}

	public static String learningModel() {
		return learningModel;
	}

	public static Learn newInstance() throws Exception {
		if (StringUtils.isNotEmpty(learningModel)) {
			throw new Exception("Learn already concreted for model:" + learningModel + ".");
		}
		return new Learn();
	}

	public static void stop() {
		stop = true;
	}

	public void execute(final String modelId) throws Exception {
		learningModel = modelId;
		stop = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Business business = new Business(emc);
			Model model = this.refreshmodel(business, modelId);
			final String modelName = model.getName();
			logger.info("神经网络多层感知机 ({}) 学习开始.", modelName);
			emc.beginTransaction(Model.class);
			model.setStatus(Model.STATUS_LEARNING);
			model.setTotalError(1.0d);
			emc.commit();
			Integer hiddenLayerCount = MapTools.getInteger(model.getPropertyMap(), Model.PROPERTY_MLP_HIDDENLAYERCOUNT,
					Model.DEFAULT_MLP_HIDDENLAYERCOUNT);
			Double maxError = MapTools.getDouble(model.getPropertyMap(), Model.PROPERTY_MLP_MAXERROR,
					Model.DEFAULT_MLP_MAXERROR);
			Integer maxIteration = MapTools.getInteger(model.getPropertyMap(), Model.PROPERTY_MLP_MAXITERATION,
					Model.DEFAULT_MLP_MAXITERATION);
			InTextBag inTextBag = this.inTextBag(business, model);
			OutTextBag outTextBag = this.outTextBag(business, model);
			try {
				NeuralNetwork<MomentumBackpropagation> neuralNetwork = model.createNeuralNetwork(inTextBag.size(),
						outTextBag.size(), hiddenLayerCount);
				neuralNetwork.getLearningRule().addListener(new LearningEventListener() {
					@Override
					public void handleLearningEvent(LearningEvent learningEvent) {
						try {
							Model o = refreshmodel(business, modelId);
							if (Objects.equals(LearningEvent.Type.EPOCH_ENDED, learningEvent.getEventType())) {
								emc.beginTransaction(Model.class);
								o.setIntermediateNnet(encode(neuralNetwork));
								logger.info("神经网络多层感知机 ({}) 学习进度 {} / {}, 总误差: {}, 单次耗时: {}.", modelName,
										neuralNetwork.getLearningRule().getCurrentIteration(), maxIteration,
										neuralNetwork.getLearningRule().getErrorFunction().getTotalError(),
										stamp.stampSeconds());
								if (stop) {
									neuralNetwork.stopLearning();
									logger.info("神经网络多层感知机 ({}) 学习停止.", modelName);
									o.setStatus("");
								} else {
									o.setStatus(Model.STATUS_LEARNING);
									o.setTotalError(neuralNetwork.getLearningRule().getErrorFunction().getTotalError());
								}
								emc.commit();
							}
						} catch (Exception e) {
							logger.error(e);
						}
					}
				});
				logger.info("神经网络多层感知机 ({}) 数据集准备就绪.", modelName);
				/* 训练数据集 */
				DataSet learnSet = this.learnOrValidationSet(business, model, inTextBag, outTextBag, Entry.TYPE_LEARN);
				this.saveDataSet(learnSet, model, Entry.TYPE_LEARN);
				/* 测试数据集 */
				DataSet validationSet = this.learnOrValidationSet(business, model, inTextBag, outTextBag,
						Entry.TYPE_VALIDATION);
				this.saveDataSet(validationSet, model, Entry.TYPE_VALIDATION);
				neuralNetwork.learn(learnSet);
				model = refreshmodel(business, modelId);
				emc.beginTransaction(Model.class);
				model.setNnet(this.encode(neuralNetwork));
				model.setInValueCount(inTextBag.size());
				model.setOutValueCount(outTextBag.size());
				emc.commit();
				this.cleanInValue(business, model);
				inTextBag.saveToInValue(business);
				this.cleanOutValue(business, model);
				outTextBag.saveToOutValue(business);
				if (logger.isDebug()) {
					File file = new File(Config.dir_local_temp(), model.getId() + ".nnet");
					neuralNetwork.save(file.getAbsolutePath());
					logger.debug("save nnet file to ", file.getAbsolutePath());
				}
				if (neuralNetwork.getLearningRule().getErrorFunction().getTotalError() > maxError) {
					logger.print("神经网络多层感知机 ({}) 学习失败, 耗时: {}, 总误差: {}, 未能达到预期值: {}.", modelName,
							stamp.consumingMilliseconds(),
							neuralNetwork.getLearningRule().getErrorFunction().getTotalError(), maxError);
					emc.beginTransaction(Model.class);
					model.setValidationMeanSquareError(
							neuralNetwork.getLearningRule().getErrorFunction().getTotalError());
					model.setStatus(Model.STATUS_EXCESSIVE);
					emc.commit();
				} else {
					logger.print("神经网络多层感知机 ({}) 学习完成.", modelName);
					if (!validationSet.isEmpty()) {
						Evaluation evaluation = new Evaluation();
						evaluation.addEvaluator(new ClassifierEvaluator.MultiClass(validationSet.getColumnNames()));
						EvaluationResult evaluationResult = evaluation.evaluateDataSet(neuralNetwork, validationSet);
						model = refreshmodel(business, modelId);
						emc.beginTransaction(Model.class);
						model.setValidationMeanSquareError(evaluationResult.getMeanSquareError());
						model.setStatus(Model.STATUS_COMPLETED);
						emc.commit();
						logger.print("神经网络多层感知机 ({}) 测试数据数量: {}, 测试结果集标准方差: {}.", modelName, validationSet.size(),
								evaluationResult.getMeanSquareError());
					} else {
						emc.beginTransaction(Model.class);
						model.setStatus(Model.STATUS_COMPLETED);
						emc.commit();
					}
					// logger.info("##############################################################################");
					// logger.info("MeanSquare Error: " +
					// evaluation.getEvaluator(ErrorEvaluator.class).getResult());
					// logger.info("##############################################################################");
					// ClassifierEvaluator classificationEvaluator = evaluation
					// .getEvaluator(ClassifierEvaluator.MultiClass.class);
					// ConfusionMatrix confusionMatrix = classificationEvaluator.getResult();
					//
					// logger.info("Confusion Matrix: \r\n" + confusionMatrix.toString());
					//
					// logger.info("##############################################################################");
					// logger.info("Classification metrics: ");
					// ClassificationMetrics[] metrics =
					// ClassificationMetrics.createFromMatrix(confusionMatrix);
					// for (ClassificationMetrics cm : metrics)
					// logger.info(cm.toString());
					//
					// logger.info("##############################################################################");
				}
			} catch (Exception e) {
				logger.error(e);
			}
		} finally {
			learningModel = "";
			stop = false;
		}
	}

	private String encode(NeuralNetwork<MomentumBackpropagation> neuralNetwork) throws Exception {
		double[] doubles = new double[NeuralNetworkCODEC.determineArraySize(neuralNetwork)];
		NeuralNetworkCODEC.network2array(neuralNetwork, doubles);
		byte[] bytes = DoubleTools.doubleToByteArray(doubles);
		return ByteTools.compressBase64String(bytes);
	}

	private Model refreshmodel(Business business, String modelId) throws Exception {
		Model model = business.entityManagerContainer().find(modelId, Model.class);
		if (null == model) {
			throw new ExceptionEntityNotExist(modelId, Model.class);
		}
		return model;
	}

	private DataSet learnOrValidationSet(Business business, Model model, InTextBag inTextBag, OutTextBag outTextBag,
			String type) throws Exception {
		DataSet data = new DataSet(inTextBag.size(), outTextBag.size());
		Entry entry = null;
		double[] ins = null;
		double[] outs = null;
		for (String id : business.entityManagerContainer().idsEqualAndEqual(Entry.class, Entry.model_FIELDNAME,
				model.getId(), Entry.type_FIELDNAME, type)) {
			entry = business.entityManagerContainer().find(id, Entry.class);
			if (null != entry) {
				ins = new double[inTextBag.size()];
				outs = new double[outTextBag.size()];
				for (Integer i : entry.getInValueLabelList()) {
					Integer idx = inTextBag.lable(i);
					if (null != idx) {
						ins[idx] = 1.0d;
					}
				}
				for (Integer i : entry.getOutValueLabelList()) {
					outs[i] = 1.0d;
				}
				data.addRow(ins, outs);
			}
		}
		return data;
	}

	/* 生成输入值包 */
	private InTextBag inTextBag(Business business, Model model) throws Exception {
		EntityManager em = business.entityManagerContainer().get(InText.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<InText> cq = cb.createQuery(InText.class);
		Root<InText> root = cq.from(InText.class);
		Predicate p = cb.equal(root.get(InText_.model), model.getId());
		cq.select(root).where(p).orderBy(cb.desc(root.get(InText_.count)));
		Integer cutoff = MapTools.getInteger(model.getPropertyMap(), Model.PROPERTY_MLP_LEARNINTEXTCUTOFFSIZE,
				Model.DEFAULT_MLP_LEARNINTEXTCUTOFFSIZE);
		List<InText> os = em.createQuery(cq.distinct(true)).setMaxResults(cutoff).getResultList();
		InTextBag inTextBag = new InTextBag(os);
		return inTextBag;
	}

	/* 生成输出值包 */
	private OutTextBag outTextBag(Business business, Model model) throws Exception {
		List<OutText> os = business.entityManagerContainer().listEqual(OutText.class, OutText.model_FIELDNAME,
				model.getId());
		os = os.stream().sorted(Comparator.comparing(OutText::getSerial, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());
		OutTextBag outTextBag = new OutTextBag(os);
		return outTextBag;
	}

	private Long cleanInValue(Business business, Model model) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(InValue.class, InValue.model_FIELDNAME,
				model.getId());
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(InValue.class);
			count = count + business.entityManagerContainer().delete(InValue.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}
	
	private void saveDataSet(DataSet dataSet, Model model, String surffix) throws Exception {
		File file = new File(Config.base(), "local/temp/" + model.getName() + "_" + surffix + ".txt");
		dataSet.save(file.getAbsolutePath());
	}

	private Long cleanOutValue(Business business, Model model) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(OutValue.class, OutValue.model_FIELDNAME,
				model.getId());
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(OutValue.class);
			count = count + business.entityManagerContainer().delete(OutValue.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	public static class OutTextBag {

		private TreeList<OutText> list = new TreeList<>();

		public OutTextBag(List<OutText> os) {
			list.addAll(os.stream().sorted(Comparator.comparing(OutText::getSerial)).collect(Collectors.toList()));
		}

		public Integer size() {
			return this.list.size();
		}

		/* 将outText转换为outValue进行保存 */
		public void saveToOutValue(Business business) throws Exception {
			List<OutValue> os = new ArrayList<>();
			for (OutText text : list) {
				OutValue outValue = new OutValue();
				outValue.setText(text.getText());
				outValue.setCount(text.getCount());
				outValue.setModel(text.getModel());
				outValue.setSerial(text.getSerial());
				os.add(outValue);
			}
			for (List<OutValue> values : ListTools.batch(os, 1000)) {
				business.entityManagerContainer().beginTransaction(OutValue.class);
				for (OutValue o : values) {
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				}
				business.entityManagerContainer().commit();
			}
		}
	}

	public static class InTextBag {

		private TreeList<InText> list = new TreeList<>();

		public InTextBag(List<InText> os) {
			list.addAll(
					os.stream().sorted(Comparator.comparing(InText::getCount).reversed()).collect(Collectors.toList()));
		}

		public Integer lable(Integer label) {
			for (int i = 0; i < list.size(); i++) {
				if (Objects.equals(label, list.get(i).getSerial())) {
					return i;
				}
			}
			return null;
		}

		public Integer size() {
			return this.list.size();
		}

		/* 将InText转换为InValue进行保存 */
		public void saveToInValue(Business business) throws Exception {
			List<InValue> os = new ArrayList<>();
			InText text = null;
			for (int i = 0; i < list.size(); i++) {
				text = list.get(i);
				InValue inValue = new InValue();
				inValue.setText(text.getText());
				inValue.setCount(text.getCount());
				inValue.setModel(text.getModel());
				inValue.setSerial(i);
				inValue.setInTextSerial(text.getSerial());
				os.add(inValue);
			}
			/* 批量保存 */
			for (List<InValue> values : ListTools.batch(os, 1000)) {
				business.entityManagerContainer().beginTransaction(InValue.class);
				for (InValue o : values) {
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				}
				business.entityManagerContainer().commit();
			}
		}
	}
}