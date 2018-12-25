package com.x.query.service.processing.jaxrs.neural.mlp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.NeuralNetworkCODEC;

import com.hankcs.hanlp.HanLP;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.ScriptHelper;
import com.x.base.core.project.tools.ByteTools;
import com.x.base.core.project.tools.DoubleTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.neural.mlp.InValue;
import com.x.query.core.entity.neural.mlp.OutValue;
import com.x.query.core.entity.neural.mlp.Project;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.ExtractTextHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

import net.sf.ehcache.Element;

class ActionCalculateWithWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCalculateWithWork.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String projectFlag, String workId) throws Exception {
		logger.debug(effectivePerson, "projectFlag:{}, workId:{}.", projectFlag, workId);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Project project = emc.flag(projectFlag, Project.class);
			if (null == project) {
				throw new ExceptionEntityNotExist(projectFlag, Project.class);
			}
			if (StringUtils.isEmpty(project.getNnet())) {
				throw new ExceptionProjectNotReady(project.getName());
			}
			NeuralNetwork<MomentumBackpropagation> neuralNetwork = null;
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), project.getId());
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				neuralNetwork = ((NeuralNetwork<MomentumBackpropagation>) element.getObjectValue());
			} else {
				if (StringUtils.isEmpty(project.getNnet())) {
					throw new ExceptionProjectNotReady(project.getName());
				}
				neuralNetwork = project.createNeuralNetwork();
				NeuralNetworkCODEC.array2network(
						DoubleTools.byteToDoubleArray(ByteTools.decompressBase64String(project.getNnet())),
						neuralNetwork);
				cache.put(new Element(cacheKey, neuralNetwork));
			}
			Wo wo = new Wo();
			Work work = emc.flag(workId, Work.class);
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			TreeSet<String> inValue = this.convert(business, project, work);
			double[] inputs = this.inputData(business, project, inValue);
			neuralNetwork.setInput(inputs);
			neuralNetwork.calculate();
			double[] outputs = neuralNetwork.getOutput();
			double mean = StatUtils.mean(outputs);
			List<Pair> pairs = new ArrayList<>();
			for (int i = 0; i < outputs.length; i++) {
				if (outputs[i] > mean) {
					Pair p = new Pair();
					p.setOut(outputs[i]);
					p.setSerial(i);
					pairs.add(p);
				}
			}
			pairs = pairs.stream().sorted(Comparator.comparing(Pair::getOut).reversed()).collect(Collectors.toList());
			Integer maxResult = (null == project.getMaxResult()) ? Project.DEFAULT_MAXRESULT : project.getMaxResult();
			if (pairs.size() > maxResult) {
				pairs = pairs.stream().limit(maxResult).collect(Collectors.toList());
			}
			wo.getValueList().addAll(this.outputData(business, project, pairs));
			result.setData(wo);
			return result;
		}
	}

	private TreeSet<String> convert(Business business, Project project, Work work) throws Exception {
		ScriptHelper scriptHelper = new ScriptHelper();
		LanguageProcessingHelper lph = new LanguageProcessingHelper();
		DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.itemCategory_FIELDNAME,
				ItemCategory.pp, Item.bundle_FIELDNAME, work.getJob());
		TreeSet<String> inValue = new TreeSet<>();
		StringBuffer text = new StringBuffer();
		text.append(converter.text(items, true, true, true, true, false, ","));
		List<Attachment> attachments = business.entityManagerContainer().listEqual(Attachment.class,
				Attachment.job_FIELDNAME, work.getJob());
		StorageMapping mapping = null;
		for (Attachment o : attachments) {
			if (o.getLength() < MAX_ATTACHMENT_BYTE_LENGTH) {
				mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
				if (null != mapping) {
					o.dumpContent(mapping);
					text.append(ExtractTextHelper.extract(o.getBytes(), o.getName(), true, true, true, false));
				}
			}
		}
		switch (StringUtils.trimToEmpty(project.getAnalyzeType())) {
		case Project.ANALYZETYPE_FULL:
			inValue.addAll(lph.word(ThisApplication.analyzer, text.toString()).stream()
					.limit(project.getGenerateInTextCutoffSize()).collect(Collectors.toList()));
			break;
		case Project.ANALYZETYPE_CUSTOMIZED:
			break;
		default:
			inValue.addAll(HanLP.extractKeyword(text.toString(), project.getGenerateInTextCutoffSize() * 2).stream()
					.filter(o -> o.length() > 1).limit(project.getGenerateInTextCutoffSize())
					.collect(Collectors.toList()));
			break;
		}
		if (StringUtils.isNotBlank(project.getInValueScriptText())) {
			scriptHelper.put(PROPERTY_INVALUES, inValue);
			scriptHelper.eval(project.getInValueScriptText());
		}
		return inValue;
	}

	private double[] inputData(Business business, Project project, TreeSet<String> inputs) throws Exception {
		List<String> values = new ArrayList<>();
		values.addAll(inputs);
		List<InValue> os = business.entityManagerContainer().listEqualAndIn(InValue.class, InValue.project_FIELDNAME,
				project.getId(), InValue.text_FIELDNAME, values);
		double[] data = new double[project.getInValueCount()];
		for (InValue in : os) {
			data[in.getSerial()] = 1.0d;
		}
		return data;
	}

	private List<String> outputData(Business business, Project project, List<Pair> pairs) throws Exception {
		List<String> list = new ArrayList<>();
		List<Integer> values = new ArrayList<>();
		for (Pair p : pairs) {
			values.add(p.getSerial());
		}
		List<OutValue> os = business.entityManagerContainer().listEqualAndIn(OutValue.class, OutValue.project_FIELDNAME,
				project.getId(), OutValue.serial_FIELDNAME, values);
		os.stream().forEach(o -> {
			list.add(o.getText());
		});
		return list;
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

	public static class Wo extends WrapStringList {

	}

}
