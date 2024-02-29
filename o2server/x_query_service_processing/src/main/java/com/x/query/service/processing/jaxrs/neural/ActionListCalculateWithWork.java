package com.x.query.service.processing.jaxrs.neural;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.NeuralNetworkCODEC;

import com.hankcs.hanlp.HanLP;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ByteTools;
import com.x.base.core.project.tools.DoubleTools;
import com.x.base.core.project.tools.MapTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.neural.InValue;
import com.x.query.core.entity.neural.Model;
import com.x.query.core.entity.neural.OutValue;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.ExtractTextHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

class ActionListCalculateWithWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListCalculateWithWork.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String modelFlag, String workId) throws Exception {
		logger.debug(effectivePerson, "modelFlag:{}, workId:{}.", modelFlag, workId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Model model = emc.flag(modelFlag, Model.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(modelFlag, Model.class);
			}
			if (StringUtils.isEmpty(model.getNnet())) {
				throw new ExceptionModelNotReady(model.getName());
			}
			NeuralNetwork<MomentumBackpropagation> neuralNetwork = null;
			CacheKey cacheKey = new CacheKey(this.getClass(), model.getId());
			Optional<?> optional = CacheManager.get(cache, cacheKey);
			if (optional.isPresent()) {
				neuralNetwork = ((NeuralNetwork<MomentumBackpropagation>) optional.get());
			} else {
				if (StringUtils.isEmpty(model.getNnet())) {
					throw new ExceptionModelNotReady(model.getName());
				}
				neuralNetwork = model.createNeuralNetwork();
				NeuralNetworkCODEC.array2network(
						DoubleTools.byteToDoubleArray(ByteTools.decompressBase64String(model.getNnet())),
						neuralNetwork);
				CacheManager.put(cache, cacheKey, neuralNetwork);
			}
			Wo wo = new Wo();
			Work work = emc.flag(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			TreeSet<String> inValue = this.convert(business, model, work);
			double[] inputs = this.inputData(business, model, inValue);
			neuralNetwork.setInput(inputs);
			neuralNetwork.calculate();
			double[] outputs = neuralNetwork.getOutput();
			// double mean = StatUtils.mean(outputs);
			List<Pair> pairs = new ArrayList<>();
			for (int i = 0; i < outputs.length; i++) {
				// if (outputs[i] > mean) {
				Pair p = new Pair();
				p.setOut(outputs[i]);
				p.setSerial(i);
				pairs.add(p);
				// }
			}
			pairs = pairs.stream().sorted(Comparator.comparing(Pair::getOut).reversed()).collect(Collectors.toList());
			Integer maxResult = MapTools.getInteger(model.getPropertyMap(), Model.PROPERTY_MLP_MAXRESULT,
					Model.DEFAULT_MLP_MAXRESULT);
			if (pairs.size() > maxResult) {
				pairs = pairs.stream().limit(maxResult).collect(Collectors.toList());
			}
			List<Wo> wos = this.outputData(business, model, pairs);
			result.setData(wos);
			return result;
		}
	}

	private TreeSet<String> convert(Business business, Model model, Work work) throws Exception {
//		ScriptHelper scriptHelper = new ScriptHelper();
		LanguageProcessingHelper lph = new LanguageProcessingHelper();
		DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.itemCategory_FIELDNAME,
				ItemCategory.pp, Item.bundle_FIELDNAME, work.getJob());
		TreeSet<String> inValue = new TreeSet<>();
		StringBuffer text = new StringBuffer();
		text.append(DataItemConverter.ItemText.text(items, true, true, true, true, false, ","));
		List<Attachment> attachments = business.entityManagerContainer().listEqual(Attachment.class,
				Attachment.job_FIELDNAME, work.getJob());
		StorageMapping mapping = null;
		for (Attachment o : attachments) {
//			if (Config.query().getCrawlWorkCompleted().getExcludeAttachment().contains(o.getName())
//					|| Config.query().getCrawlWorkCompleted().getExcludeSite().contains(o.getSite())
//					|| StringUtils.equalsIgnoreCase(o.getName(), Config.processPlatform().getDocToWordDefaultFileName())
//					|| StringUtils.equalsIgnoreCase(o.getSite(), Config.processPlatform().getDocToWordDefaultSite())) {
//				continue;
//			}
			if (o.getLength() < MAX_ATTACHMENT_BYTE_LENGTH) {
				mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
				if (null != mapping) {
					o.dumpContent(mapping);
					text.append(ExtractTextHelper.extract(o.getBytes(), o.getName(), true, true, true, false));
				}
			}
		}
		switch (StringUtils.trimToEmpty(model.getAnalyzeType())) {
		case Model.ANALYZETYPE_FULL:
			lph.word(text.toString()).stream().limit(MapTools.getInteger(model.getPropertyMap(),
					Model.PROPERTY_MLP_GENERATEINTEXTCUTOFFSIZE, Model.DEFAULT_MLP_GENERATEINTEXTCUTOFFSIZE))
					.forEach(o -> {
						inValue.add(o.getValue());
					});
			break;
		case Model.ANALYZETYPE_CUSTOMIZED:
			break;
		default:
			inValue.addAll(HanLP
					.extractKeyword(text.toString(),
							MapTools.getInteger(model.getPropertyMap(), Model.PROPERTY_MLP_GENERATEINTEXTCUTOFFSIZE,
									Model.DEFAULT_MLP_GENERATEINTEXTCUTOFFSIZE) * 2)
					.stream().filter(o -> o.length() > 1).limit(MapTools.getInteger(model.getPropertyMap(),
							Model.PROPERTY_MLP_GENERATEINTEXTCUTOFFSIZE, Model.DEFAULT_MLP_GENERATEINTEXTCUTOFFSIZE))
					.collect(Collectors.toList()));
			break;
		}
//		if (StringUtils.isNotBlank(model.getInValueScriptText())) {
//			// ScriptContext scriptContext =
//			// ScriptingFactory.scriptContextEvalInitialServiceScript();
//			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings.putMember(
//					PROPERTY_INVALUES, inValue);
//			JsonScriptingExecutor.eval(model.getInValueScriptText(), bindings);
//		}
		return inValue;
	}

	private double[] inputData(Business business, Model model, TreeSet<String> inputs) throws Exception {
		List<String> values = new ArrayList<>();
		values.addAll(inputs);
		List<InValue> os = business.entityManagerContainer().listEqualAndIn(InValue.class, InValue.model_FIELDNAME,
				model.getId(), InValue.text_FIELDNAME, values);
		double[] data = new double[model.getInValueCount()];
		for (InValue in : os) {
			data[in.getSerial()] = 1.0d;
		}
		return data;
	}

	private List<Wo> outputData(Business business, Model model, List<Pair> pairs) throws Exception {
		List<Wo> wos = new ArrayList<>();
		List<Integer> values = new ArrayList<>();
		for (Pair p : pairs) {
			values.add(p.getSerial());
		}
		List<OutValue> os = business.entityManagerContainer().listEqualAndIn(OutValue.class, OutValue.model_FIELDNAME,
				model.getId(), OutValue.serial_FIELDNAME, values);
		String text = "";
		for (Pair pair : pairs) {
			text = "";
			for (OutValue o : os) {
				if (Objects.equals(o.getSerial(), pair.getSerial())) {
					text = o.getText();
					break;
				}
			}
			if (StringUtils.isNotEmpty(text)) {
				Wo wo = new Wo();
				wo.setScore(pair.getOut());
				wo.setValue(text);
				wos.add(wo);
			}
		}
		return wos;
	}

	public static class Pair {
		Integer serial;
		Double out;

		public Integer getSerial() {
			return serial;
		}

		public void setSerial(Integer serial) {
			this.serial = serial;
		}

		public Double getOut() {
			return out;
		}

		public void setOut(Double out) {
			this.out = out;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private Double score;
		private String value;

		public Double getScore() {
			return score;
		}

		public void setScore(Double score) {
			this.score = score;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
