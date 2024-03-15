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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Source;

import com.hankcs.hanlp.HanLP;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.MapTools;
import com.x.base.core.project.tools.StringTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.cms.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.neural.Entry;
import com.x.query.core.entity.neural.InText;
import com.x.query.core.entity.neural.Model;
import com.x.query.core.entity.neural.OutText;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.ExtractTextHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

public class Generate {

	private static Logger logger = LoggerFactory.getLogger(Generate.class);

	private volatile static String generatingModel = "";

	private volatile static boolean stop = false;

	private Generate() {

	}

	public static String generatingModel() {
		return generatingModel;
	}

	public static void stop() {
		stop = true;
	}

	public static Generate newInstance() throws Exception {
		if (StringUtils.isNotEmpty(generatingModel)) {
			throw new Exception("Generate already concreted for model:" + generatingModel + ".");
		}
		return new Generate();
	}

	protected void execute(final String modelId) throws Exception {
		generatingModel = modelId;
		stop = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Business business = new Business(emc);
			Model model = refreshModel(business, modelId);
			logger.info("神经网络多层感知机 ({}) 生成条目开始.", model.getName());
			if (StringUtils.equals(Model.STATUS_GENERATING, model.getStatus())) {
				throw new ExceptionGenerate(model.getName());
			}
			final Double validationRate = (MapTools.getDouble(model.getPropertyMap(), Model.PROPERTY_MLP_VALIDATIONRATE,
					Model.DEFAULT_MLP_VALIDATIONRATE));
			List<String> bundles = this.listBundle(business, model);
			if (ListTools.isEmpty(bundles)) {
				throw new ExceptionBundleEmpty(model.getName());
			}
			/* 用于测试的bundle */
			List<String> validationBundles = ListTools.randomWithRate(bundles, validationRate);
			/* 用于学习的bandle */
			List<String> learnBundles = ListUtils.subtract(bundles, validationBundles);
			emc.beginTransaction(Model.class);
			model.setStatus(Model.STATUS_GENERATING);
			model.setGeneratingPercent(0);
			model.setValidationEntryCount(0);
			model.setLearnEntryCount(0);
			emc.commit();
			/* 准备运算,清空数据 */
			this.clean(business, model);
			String text = model.getOutValueScriptText();
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings();
			Source outValueCompiledScript = GraalvmScriptingFactory.functionalization(text);
			Source inValueCompiledScript = null;
			if (StringUtils.isNotEmpty(model.getInValueScriptText())) {
				inValueCompiledScript = GraalvmScriptingFactory.functionalization(model.getInValueScriptText());
			}
			Source attachmentCompiledScript = null;
			if (StringUtils.isNotEmpty(model.getAttachmentScriptText())) {
				attachmentCompiledScript = GraalvmScriptingFactory.functionalization(model.getAttachmentScriptText());
			}

			LanguageProcessingHelper lph = new LanguageProcessingHelper();
			DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
			TreeSet<String> inValues = new TreeSet<>();
			TreeSet<String> outValues = new TreeSet<>();
			InBag inBag = new InBag();
			OutBag outBag = new OutBag();
			int learnEntryCount = 0;
			int testEntryCount = 0;
			int total = 0;
			for (int i = 0; i < learnBundles.size(); i++) {
				WorkCompleted workCompleted = emc.find(bundles.get(i), WorkCompleted.class);
				if (null != workCompleted) {
					inValues.clear();
					outValues.clear();
					this.convert(business, converter, outValueCompiledScript, inValueCompiledScript,
							attachmentCompiledScript, lph, model, workCompleted, inValues, outValues);
					if ((!inValues.isEmpty()) && (!outValues.isEmpty())) {
						this.createLearnEntry(business, model, workCompleted, inBag, outBag, inValues, outValues);
						learnEntryCount++;
					}
				}
				if (total % 100 == 99) {
					if (checkStop(business, modelId, bundles, total)) {
						return;
					}
				}
				total++;
			}
			for (int i = 0; i < validationBundles.size(); i++) {
				WorkCompleted workCompleted = emc.find(bundles.get(i), WorkCompleted.class);
				if (null != workCompleted) {
					inValues.clear();
					outValues.clear();
					this.convert(business, converter, outValueCompiledScript, inValueCompiledScript,
							attachmentCompiledScript, lph, model, workCompleted, inValues, outValues);
					if ((!inValues.isEmpty()) && (!outValues.isEmpty())) {
						this.createValidationEntry(business, model, workCompleted, inBag, outBag, inValues, outValues);
						testEntryCount++;
					}
				}
				if (total % 100 == 99) {
					if (checkStop(business, modelId, bundles, total)) {
						return;
					}
				}
				total++;
			}
			inBag.save(business, model);
			outBag.save(business, model);
			model = this.refreshModel(business, modelId);
			emc.beginTransaction(Model.class);
			model.setStatus("");
			model.setEntryCount(total);
			model.setEffectiveEntryCount(bundles.size());
			model.setGeneratingPercent(100);
			model.setLearnEntryCount(learnEntryCount);
			model.setValidationEntryCount(testEntryCount);
			emc.check(model, CheckPersistType.all);
			emc.commit();
			logger.info("神经网络多层感知机 ({}) 完成条目生成, 耗时: {}.", model.getName(), stamp.consumingMilliseconds());
		} finally {
			generatingModel = "";
			stop = false;
		}
	}

	private boolean checkStop(Business business, final String modelId, List<String> bundles, int i) throws Exception {
		Model model = this.refreshModel(business, modelId);
		if (stop) {
			business.entityManagerContainer().beginTransaction(Model.class);
			model.setStatus("");
			business.entityManagerContainer().commit();
			logger.info("神经网络多层感知机 ({}) 项目条目生成过程被取消.", model.getName());
			return true;
		} else {
			business.entityManagerContainer().beginTransaction(Model.class);
			model.setStatus(Model.STATUS_GENERATING);
			int percent = (int) (Math.ceil(i * 100.0 / bundles.size()));
			model.setGeneratingPercent(percent);
			logger.info("神经网络多层感知机 ({}) 条目生成进度 {}%, 共计: {} 个条目.", model.getName(), percent, bundles.size());
			business.entityManagerContainer().commit();
			return false;
		}
	}

	public static class InBag {

		private TreeList<String> list = new TreeList<>();

		private Map<String, Integer> map = new HashMap<>();

		public Integer test(String str) {
			return list.indexOf(str);
		}

		public Integer learn(String str) {
			int idx = list.indexOf(str);
			if (idx > -1) {
				map.put(str, map.get(str) + 1);
			} else {
				idx = list.size();
				list.add(str);
				map.put(str, 1);
			}
			return idx;
		}

		public void save(Business business, Model model) throws Exception {
			List<InText> list = this.convert();
			for (List<InText> os : ListTools.batch(list, 2000)) {
				business.entityManagerContainer().beginTransaction(InText.class);
				for (InText o : os) {
					o.setModel(model.getId());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				}
				business.entityManagerContainer().commit();
			}
		}

		private List<InText> convert() {
			List<InText> os = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				String str = list.get(i);
				InText text = new InText();
				text.setText(str);
				text.setSerial(i);
				text.setCount(map.get(str));
				os.add(text);
			}
			return os;
		}
	}

	public static class OutBag {

		private TreeList<String> list = new TreeList<>();
		private Map<String, Integer> map = new HashMap<>();

		public Integer test(String str) {
			return list.indexOf(str);
		}

		public Integer learn(String str) {
			int idx = list.indexOf(str);
			if (idx > -1) {
				map.put(str, map.get(str) + 1);
			} else {
				idx = list.size();
				list.add(str);
				map.put(str, 1);
			}
			return idx;
		}

		public void save(Business business, Model model) throws Exception {
			List<OutText> list = this.convert();
			for (List<OutText> os : ListTools.batch(list, 2000)) {
				business.entityManagerContainer().beginTransaction(OutText.class);
				for (OutText o : os) {
					o.setModel(model.getId());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				}
				business.entityManagerContainer().commit();
			}
		}

		private List<OutText> convert() {
			List<OutText> os = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				String str = list.get(i);
				OutText text = new OutText();
				text.setText(str);
				text.setSerial(i);
				text.setCount(map.get(str));
				os.add(text);
			}
			return os;
		}

	}

	private List<String> listBundle(Business business, Model model) throws Exception {
		if (StringUtils.equals(model.getDataType(), Model.DATATYPE_CMS)) {
			return listCmsBundle(business, model);
		} else {
			return listProcessPlatformBundle(business, model);
		}

	}

	private List<String> listCmsBundle(Business business, Model model) throws Exception {
		return null;
	}

	private List<String> listProcessPlatformBundle(Business business, Model model) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(model.getApplicationList())) {
			p = cb.and(p,
					cb.isMember(root.get(WorkCompleted.application_FIELDNAME), cb.literal(model.getApplicationList())));
		}
		if (ListTools.isNotEmpty(model.getProcessList())) {
			p = cb.and(p, cb.isMember(root.get(WorkCompleted.process_FIELDNAME), cb.literal(model.getProcessList())));
		}
		if (null != model.getStartDate()) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(WorkCompleted_.startTime), model.getStartDate()));
		}
		if (null != model.getEndDate()) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(WorkCompleted_.startTime), model.getEndDate()));
		}
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private Model refreshModel(Business business, String modelId) throws Exception {
		Model model = business.entityManagerContainer().find(modelId, Model.class);
		if (null == model) {
			throw new ExceptionEntityNotExist(modelId, Model.class);
		}
		return model;
	}

	private void clean(Business business, Model model) throws Exception {
		Long cleanInText = this.cleanInText(business, model.getId());
		Long cleanOutText = this.cleanOutText(business, model.getId());
		Long cleanEntryCount = this.cleanEntry(business, model.getId());
		logger.print("神经网络多层感知机 ({}) 清理训练数据集, entry: {}, inText: {}, outText: {}.", model.getName(), cleanEntryCount,
				cleanInText, cleanOutText);
	}

	private Long cleanEntry(Business business, String modelId) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Entry.class, Entry.model_FIELDNAME, modelId);
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(Entry.class);
			count = count + business.entityManagerContainer().delete(Entry.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	private Long cleanInText(Business business, String modelId) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(InText.class, InText.model_FIELDNAME, modelId);
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(InText.class);
			count = count + business.entityManagerContainer().delete(InText.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	private Long cleanOutText(Business business, String modelId) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(OutText.class, OutText.model_FIELDNAME, modelId);
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(OutText.class);
			count = count + business.entityManagerContainer().delete(OutText.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	private void convert(Business business, DataItemConverter<Item> converter, Source outValueCompiledScript,
			Source inValueCompiledScript, Source attachmentCompiledScript, LanguageProcessingHelper lph, Model model,
			WorkCompleted workCompleted, TreeSet<String> inValue, TreeSet<String> outValue) throws Exception {
		logger.debug("神经网络多层感知机 ({}) 正在生成条目: {}.", model.getName(), workCompleted.getTitle());
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.itemCategory_FIELDNAME,
				ItemCategory.pp, Item.bundle_FIELDNAME, workCompleted.getJob());
		/* 先计算output,在后面可以在data的text先把output替换掉 */
		Data data = XGsonBuilder.convert(converter.assemble(items), Data.class);
		GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
				.putMember(GraalvmScriptingFactory.BINDING_NAME_DATA, data);
		outValue.addAll(GraalvmScriptingFactory.evalAsStrings(attachmentCompiledScript, bindings));
		StringBuilder text = new StringBuilder();
		String dataText = DataItemConverter.ItemText.text(items, true, true, true, true, true, ",");
		dataText = StringUtils.replaceEach(dataText, outValue.toArray(new String[outValue.size()]),
				StringTools.fill(outValue.size(), ","));
		text.append(dataText);
		List<Attachment> attachmentObjects = business.entityManagerContainer().listEqual(Attachment.class,
				Attachment.job_FIELDNAME, workCompleted.getJob());
		/* 把不需要的附件过滤掉 */
		if (null != attachmentCompiledScript) {
			List<String> attachments = ListTools.extractProperty(attachmentObjects, Attachment.name_FIELDNAME,
					String.class, true, true);
			bindings.putMember(BaseAction.PROPERTY_ATTACHMENTS, attachments);
			GraalvmScriptingFactory.eval(attachmentCompiledScript, bindings);
			attachmentObjects = ListTools.removePropertyNotIn(attachmentObjects, Attachment.name_FIELDNAME,
					attachments);
		}
		for (Attachment att : attachmentObjects) {
//			if (Config.query().getCrawlWorkCompleted().getExcludeAttachment().contains(att.getName())
//					|| Config.query().getCrawlWorkCompleted().getExcludeSite().contains(att.getSite())
//					|| StringUtils.equalsIgnoreCase(att.getName(),
//							Config.processPlatform().getDocToWordDefaultFileName())
//					|| StringUtils.equalsIgnoreCase(att.getSite(),
//							Config.processPlatform().getDocToWordDefaultSite())) {
//				continue;
//			}
			// 文件小于10M
			if (att.getLength() < BaseAction.MAX_ATTACHMENT_BYTE_LENGTH) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						att.getStorage());
				if (null != mapping) {
					att.dumpContent(mapping);
					text.append(ExtractTextHelper.extract(att.getBytes(), att.getName(), true, true, true, false));
				}
			}
		}
		switch (StringUtils.trimToEmpty(model.getAnalyzeType())) {
		case Model.ANALYZETYPE_FULL:
			lph.word(text.toString()).stream().limit(MapTools.getInteger(model.getPropertyMap(),
					Model.PROPERTY_MLP_GENERATEINTEXTCUTOFFSIZE, Model.DEFAULT_MLP_GENERATEINTEXTCUTOFFSIZE))
					.forEach(w -> {
						inValue.add(w.getValue());
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
		if (null != inValueCompiledScript) {
			bindings.putMember(BaseAction.PROPERTY_INVALUES, inValue);
			GraalvmScriptingFactory.eval(inValueCompiledScript, bindings);
		}
	}

	private void createLearnEntry(Business business, Model model, WorkCompleted workCompleted, InBag inBag,
			OutBag outBag, TreeSet<String> inValues, TreeSet<String> outValues) throws Exception {
		Entry entry = new Entry();
		entry.setType(Entry.TYPE_LEARN);
		entry.setInValueLabelList(new ArrayList<Integer>());
		entry.setOutValueLabelList(new ArrayList<Integer>());
		entry.setBundle(workCompleted.getId());
		entry.setModel(model.getId());
		entry.setTitle(workCompleted.getTitle());
		entry.setInValueCount(inValues.size());
		entry.setOutValueCount(outValues.size());
		for (String s : inValues) {
			entry.getInValueLabelList().add(inBag.learn(s));
		}
		for (String s : outValues) {
			entry.getOutValueLabelList().add(outBag.learn(s));
		}
		business.entityManagerContainer().beginTransaction(Entry.class);
		business.entityManagerContainer().persist(entry);
		business.entityManagerContainer().commit();
	}

	private void createValidationEntry(Business business, Model model, WorkCompleted workCompleted, InBag inBag,
			OutBag outBag, TreeSet<String> inValues, TreeSet<String> outValues) throws Exception {
		Entry entry = new Entry();
		entry.setType(Entry.TYPE_VALIDATION);
		entry.setInValueLabelList(new ArrayList<Integer>());
		entry.setOutValueLabelList(new ArrayList<Integer>());
		entry.setBundle(workCompleted.getId());
		entry.setModel(model.getId());
		entry.setTitle(workCompleted.getTitle());
		entry.setInValueCount(inValues.size());
		entry.setOutValueCount(outValues.size());
		for (String s : inValues) {
			Integer idx = inBag.test(s);
			if (idx > -1) {
				entry.getInValueLabelList().add(idx);
			}
		}
		for (String s : outValues) {
			Integer idx = outBag.test(s);
			if (idx > -1) {
				entry.getOutValueLabelList().add(idx);
			}
		}
		business.entityManagerContainer().beginTransaction(Entry.class);
		business.entityManagerContainer().persist(entry);
		business.entityManagerContainer().commit();
	}
}