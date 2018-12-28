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
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.scripting.ScriptHelper;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.base.core.project.utils.time.TimeStamp;
import com.x.cms.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.neural.Entry;
import com.x.query.core.entity.neural.InText;
import com.x.query.core.entity.neural.OutText;
import com.x.query.core.entity.neural.Project;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.helper.ExtractTextHelper;
import com.x.query.service.processing.helper.LanguageProcessingHelper;

public class GenerateQueue extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(GenerateQueue.class);

	@Override
	protected void execute(final String projectId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			TimeStamp stamp = new TimeStamp();
			Business business = new Business(emc);
			Project project = refreshProject(business, projectId);
			logger.info("神经网络多层感知机 ({}) 生成条目开始.", project.getName());
			if (StringUtils.equals(Project.STATUS_GENERATING, project.getStatus())) {
				throw new ExceptionGenerating(project.getName());
			}
			if (StringUtils.equals(Project.STATUS_LEARNING, project.getStatus())) {
				throw new ExceptionLearning(project.getName());
			}
			ThisApplication.generating_stop_tag.remove(project.getId());
			final Double validationRate = project.getPropertyMap().getDouble(Project.PROPERTY_MLP_VALIDATIONRATE,
					Project.DEFAULT_MLP_VALIDATIONRATE);
			List<String> bundles = this.listBundle(business, project);
			if (ListTools.isEmpty(bundles)) {
				throw new ExceptionBundleEmpty(project.getName());
			}
			/* 用于测试的bundle */
			List<String> validateBundles = ListTools.randomWithRate(bundles, validationRate);
			/* 用于学习的bandle */
			List<String> learnBundles = ListUtils.subtract(bundles, validateBundles);
			emc.beginTransaction(Project.class);
			project.setStatus(Project.STATUS_GENERATING);
			project.setGeneratingPercent(0);
			project.setValidationEntryCount(0);
			project.setLearnEntryCount(0);
			emc.commit();
			/* 准备运算,清空数据 */
			this.clean(business, project);
			ScriptHelper scriptHelper = new ScriptHelper();
			LanguageProcessingHelper lph = new LanguageProcessingHelper();
			DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);
			TreeSet<String> inValues = new TreeSet<>();
			TreeSet<String> outValues = new TreeSet<>();
			InBag inBag = new InBag();
			OutBag outBag = new OutBag();
			int learnEntryCount = 0;
			int validationEntryCount = 0;
			int total = 0;
			for (int i = 0; i < learnBundles.size(); i++) {
				WorkCompleted workCompleted = emc.find(bundles.get(i), WorkCompleted.class);
				if (null != workCompleted) {
					inValues.clear();
					outValues.clear();
					this.convert(business, converter, scriptHelper, lph, project, workCompleted, inValues, outValues);
					if ((!inValues.isEmpty()) && (!outValues.isEmpty())) {
						this.createLearnEntry(business, project, workCompleted, inBag, outBag, inValues, outValues);
						learnEntryCount++;
					}
				}
				if (total % 100 == 99) {
					if (checkStop(business, projectId, bundles, total)) {
						return;
					}
				}
				total++;
			}
			for (int i = 0; i < validateBundles.size(); i++) {
				WorkCompleted workCompleted = emc.find(bundles.get(i), WorkCompleted.class);
				if (null != workCompleted) {
					inValues.clear();
					outValues.clear();
					this.convert(business, converter, scriptHelper, lph, project, workCompleted, inValues, outValues);
					if ((!inValues.isEmpty()) && (!outValues.isEmpty())) {
						this.createValidationEntry(business, project, workCompleted, inBag, outBag, inValues,
								outValues);
						validationEntryCount++;
					}
				}
				if (total % 100 == 99) {
					if (checkStop(business, projectId, bundles, total)) {
						return;
					}
				}
				total++;
			}
			inBag.save(business, project);
			outBag.save(business, project);
			project = this.refreshProject(business, projectId);
			emc.beginTransaction(Project.class);
			project.setStatus("");
			project.setEntryCount(bundles.size());
			project.setGeneratingPercent(100);
			project.setLearnEntryCount(learnEntryCount);
			project.setValidationEntryCount(validationEntryCount);
			emc.check(project, CheckPersistType.all);
			emc.commit();
			logger.info("神经网络多层感知机 ({}) 完成条目生成, 耗时: {}.", project.getName(), stamp.consumingMilliseconds());
		}
	}

	private boolean checkStop(Business business, final String projectId, List<String> bundles, int i) throws Exception {
		Project project = this.refreshProject(business, projectId);
		if (ThisApplication.generating_stop_tag.remove(project.getId())) {
			business.entityManagerContainer().beginTransaction(Project.class);
			project.setStatus("");
			business.entityManagerContainer().commit();
			logger.info("神经网络多层感知机 ({}) 项目条目生成过程被取消.", project.getName());
			return true;
		} else {
			business.entityManagerContainer().beginTransaction(Project.class);
			project.setStatus(Project.STATUS_GENERATING);
			int percent = (int) (Math.ceil(i * 100.0 / bundles.size()));
			project.setGeneratingPercent(percent);
			logger.info("神经网络多层感知机 ({}) 条目生成进度 {}%, 共计: {} 个条目.", project.getName(), percent, bundles.size());
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

		public void save(Business business, Project project) throws Exception {
			List<InText> list = this.convert();
			for (List<InText> os : ListTools.batch(list, 2000)) {
				business.entityManagerContainer().beginTransaction(InText.class);
				for (InText o : os) {
					o.setProject(project.getId());
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

		public void save(Business business, Project project) throws Exception {
			List<OutText> list = this.convert();
			for (List<OutText> os : ListTools.batch(list, 2000)) {
				business.entityManagerContainer().beginTransaction(OutText.class);
				for (OutText o : os) {
					o.setProject(project.getId());
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

	private List<String> listBundle(Business business, Project project) throws Exception {
		if (StringUtils.equals(project.getType(), Project.TYPE_CMS)) {
			return listCmsBundle(business, project);
		} else {
			return listProcessPlatformBundle(business, project);
		}

	}

	private List<String> listCmsBundle(Business business, Project project) throws Exception {
		return null;
	}

	private List<String> listProcessPlatformBundle(Business business, Project project) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(project.getApplicationList())) {
			p = cb.and(p, cb.isMember(root.get(WorkCompleted.application_FIELDNAME),
					cb.literal(project.getApplicationList())));
		}
		if (ListTools.isNotEmpty(project.getProcessList())) {
			p = cb.and(p, cb.isMember(root.get(WorkCompleted.process_FIELDNAME), cb.literal(project.getProcessList())));
		}
		if (null != project.getStartDate()) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(WorkCompleted_.startTime), project.getStartDate()));
		}
		if (null != project.getEndDate()) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(WorkCompleted_.startTime), project.getEndDate()));
		}
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private Project refreshProject(Business business, String projectId) throws Exception {
		Project project = business.entityManagerContainer().find(projectId, Project.class);
		if (null == project) {
			throw new ExceptionEntityNotExist(projectId, Project.class);
		}
		return project;
	}

	private void clean(Business business, Project project) throws Exception {
		Long cleanInText = this.cleanInText(business, project.getId());
		Long cleanOutText = this.cleanOutText(business, project.getId());
		Long cleanEntryCount = this.cleanEntry(business, project.getId());
		logger.print("神经网络多层感知机 ({}) 清理训练数据集, entry: {}, inText: {}, outText: {}.", project.getName(), cleanEntryCount,
				cleanInText, cleanOutText);
	}

	private Long cleanEntry(Business business, String projectId) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Entry.class, Entry.project_FIELDNAME, projectId);
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(Entry.class);
			count = count + business.entityManagerContainer().delete(Entry.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	private Long cleanInText(Business business, String projectId) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(InText.class, InText.project_FIELDNAME,
				projectId);
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(InText.class);
			count = count + business.entityManagerContainer().delete(InText.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	private Long cleanOutText(Business business, String projectId) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(OutText.class, OutText.project_FIELDNAME,
				projectId);
		Long count = 0L;
		for (List<String> os : ListTools.batch(ids, 2000)) {
			business.entityManagerContainer().beginTransaction(OutText.class);
			count = count + business.entityManagerContainer().delete(OutText.class, os);
			business.entityManagerContainer().commit();
		}
		return count;
	}

	private void convert(Business business, DataItemConverter<Item> converter, ScriptHelper scriptHelper,
			LanguageProcessingHelper lph, Project project, WorkCompleted workCompleted, TreeSet<String> inValue,
			TreeSet<String> outValue) throws Exception {
		logger.debug("神经网络多层感知机 ({}) 正在生成条目: {}.", project.getName(), workCompleted.getTitle());
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.itemCategory_FIELDNAME,
				ItemCategory.pp, Item.bundle_FIELDNAME, workCompleted.getJob());
		/* 先计算output,在后面可以在data的text先把output替换掉 */
		Data data = XGsonBuilder.convert(converter.assemble(items), Data.class);
		scriptHelper.put(BaseAction.PROPERTY_WORKCOMPLETED, workCompleted);
		scriptHelper.put(BaseAction.PROPERTY_DATA, data);
		if (StringUtils.isNotBlank(project.getOutValueScriptText())) {
			scriptHelper.put(BaseAction.PROPERTY_OUTVALUES, outValue);
			scriptHelper.eval(project.getOutValueScriptText());
		}
		StringBuffer text = new StringBuffer();
		String dataText = converter.text(items, true, true, true, true, true, ",");
		dataText = StringUtils.replaceEach(dataText, outValue.toArray(new String[outValue.size()]),
				StringTools.fill(outValue.size(), ","));
		text.append(dataText);
		// logger.debug("{} data text:{}.", workCompleted.getTitle(), dataText);
		// inValue.addAll(lph.word(ThisApplication.analyzer, dataText));
		List<Attachment> attachmentObjects = business.entityManagerContainer().listEqual(Attachment.class,
				Attachment.job_FIELDNAME, workCompleted.getJob());
		/* 把不需要的附件过滤掉 */
		if (StringUtils.isNotBlank(project.getAttachmentScriptText())) {
			List<String> attachments = ListTools.extractProperty(attachmentObjects, Attachment.name_FIELDNAME,
					String.class, true, true);
			scriptHelper.put(BaseAction.PROPERTY_ATTACHMENTS, attachments);
			scriptHelper.eval(project.getAttachmentScriptText());
			attachmentObjects = ListTools.removePropertyNotIn(attachmentObjects, Attachment.name_FIELDNAME,
					attachments);
		}
		for (Attachment o : attachmentObjects) {
			/* 文件小于5M */
			if (o.getLength() < BaseAction.MAX_ATTACHMENT_BYTE_LENGTH) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						o.getStorage());
				if (null != mapping) {
					o.dumpContent(mapping);
					// logger.debug("{} attachment text:{}.", workCompleted.getTitle(),
					// attachmentText);
					text.append(ExtractTextHelper.extract(o.getBytes(), o.getName(), true, true, true, false));
				}
			}
		}
		Integer generateInTextCutoffSize = project.getPropertyMap().getInteger(
				Project.PROPERTY_MLP_GENERATEINTEXTCUTOFFSIZE, Project.DEFAULT_MLP_GENERATEINTEXTCUTOFFSIZE);
		switch (StringUtils.trimToEmpty(project.getAnalyzeType())) {
		case Project.ANALYZETYPE_FULL:
			inValue.addAll(lph.word(ThisApplication.analyzer, text.toString()).stream().limit(generateInTextCutoffSize)
					.collect(Collectors.toList()));
			break;
		case Project.ANALYZETYPE_CUSTOMIZED:
			break;
		default:
			inValue.addAll(HanLP.extractKeyword(text.toString(), generateInTextCutoffSize * 2).stream()
					.filter(o -> o.length() > 1).limit(generateInTextCutoffSize).collect(Collectors.toList()));
			break;
		}
		if (StringUtils.isNotBlank(project.getInValueScriptText())) {
			scriptHelper.put(BaseAction.PROPERTY_INVALUES, inValue);
			scriptHelper.eval(project.getInValueScriptText());
		}
	}

	private void createLearnEntry(Business business, Project project, WorkCompleted workCompleted, InBag inBag,
			OutBag outBag, TreeSet<String> inValues, TreeSet<String> outValues) throws Exception {
		Entry entry = new Entry();
		entry.setType(Entry.TYPE_LEARN);
		entry.setInValueLabelList(new ArrayList<Integer>());
		entry.setOutValueLabelList(new ArrayList<Integer>());
		entry.setBundle(workCompleted.getId());
		entry.setProject(project.getId());
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

	private void createValidationEntry(Business business, Project project, WorkCompleted workCompleted, InBag inBag,
			OutBag outBag, TreeSet<String> inValues, TreeSet<String> outValues) throws Exception {
		Entry entry = new Entry();
		entry.setType(Entry.TYPE_VALIDATION);
		entry.setInValueLabelList(new ArrayList<Integer>());
		entry.setOutValueLabelList(new ArrayList<Integer>());
		entry.setBundle(workCompleted.getId());
		entry.setProject(project.getId());
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