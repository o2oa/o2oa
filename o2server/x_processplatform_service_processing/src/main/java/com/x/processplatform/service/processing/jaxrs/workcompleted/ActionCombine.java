package com.x.processplatform.service.processing.jaxrs.workcompleted;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.query.core.entity.Item;

class ActionCombine extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCombine.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			WorkCompleted workCompleted = emc.fetch(id, WorkCompleted.class,
					ListTools.toList(WorkCompleted.job_FIELDNAME));
			if (null == workCompleted) {
				throw new ExceptionEntityNotExist(id, WorkCompleted.class);
			}
			executorSeed = workCompleted.getJob();
		}

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(new CallableAction(id)).get();
	}

	public static class Wo extends WoId {
	}

	public class CallableAction implements Callable<ActionResult<Wo>> {

		private String id;

		CallableAction(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			WorkCompleted workCompleted = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				workCompleted = emc.find(id, WorkCompleted.class);
				Business business = new Business(emc);
				if (null != workCompleted) {
					emc.beginTransaction(WorkCompleted.class);
					List<Item> items = mergeItem(business, workCompleted);
					List<WorkLog> workLogs = mergeWorkLog(business, workCompleted);
					List<Record> records = mergeRecord(business, workCompleted);
					List<DocumentVersion> documentVersions = listDocumentVersion(business, workCompleted);
					workCompleted.setMerged(true);
					emc.commit();
					this.remove(business, items, workLogs, records,documentVersions);
					logger.print("已完成工作合并, id: {}, title:{}, sequence:{}.", workCompleted.getId(),
							workCompleted.getTitle(), workCompleted.getSequence());
				}
			} catch (Exception e) {
				throw new ExceptionCombine(e, id);
			}
			Wo wo = new Wo();
			wo.setId(id);
			ActionResult<Wo> result = new ActionResult<>();
			result.setData(wo);
			return result;
		}

		private void remove(Business business, List<Item> items, List<WorkLog> workLogs, List<Record> records,List<DocumentVersion> documentVersions)
				throws Exception {
			EntityManagerContainer emc = business.entityManagerContainer();
			if (!items.isEmpty()) {
				emc.beginTransaction(Item.class);
				for (Item o : items) {
					emc.remove(o, CheckRemoveType.all);
				}
			}
			if (!workLogs.isEmpty()) {
				emc.beginTransaction(WorkLog.class);
				for (WorkLog o : workLogs) {
					emc.remove(o, CheckRemoveType.all);
				}
			}
			if (!records.isEmpty()) {
				emc.beginTransaction(Record.class);
				for (Record o : records) {
					emc.remove(o, CheckRemoveType.all);
				}
			}
	
			if (!documentVersions.isEmpty()) {
				emc.beginTransaction(DocumentVersion.class);
				for (DocumentVersion o : documentVersions) {
					emc.remove(o, CheckRemoveType.all);
				}
			}
			emc.commit();
		}

		private List<DocumentVersion> listDocumentVersion(Business business, WorkCompleted workCompleted) throws Exception {
			return  business.entityManagerContainer().listEqual(DocumentVersion.class, DocumentVersion.job_FIELDNAME,
					workCompleted.getJob());
		}

		private List<Item> mergeItem(Business business, WorkCompleted workCompleted) throws Exception {
			List<Item> list = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.bundle_FIELDNAME,
					workCompleted.getJob(), Item.itemCategory_FIELDNAME, ItemCategory.pp);
			DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);
			JsonElement jsonElement = converter.assemble(list);
			workCompleted.getProperties().setData(gson.fromJson(jsonElement, Data.class));
			return list;
		}

		private List<Record> mergeRecord(Business business, WorkCompleted workCompleted) throws Exception {
			List<Record> list = business.entityManagerContainer().listEqual(Record.class, Record.job_FIELDNAME,
					workCompleted.getJob());
			Collections.sort(list, Comparator.comparing(Record::getOrder, Comparator.nullsLast(Long::compareTo)));
			workCompleted.getProperties().setRecordList(list);
			return list;
		}

		private List<WorkLog> mergeWorkLog(Business business, WorkCompleted workCompleted) throws Exception {
			List<WorkLog> list = business.entityManagerContainer().listEqual(WorkLog.class, WorkCompleted.job_FIELDNAME,
					workCompleted.getJob());
			Collections.sort(list, Comparator.comparing(WorkLog::getCreateTime, Comparator.nullsLast(Date::compareTo)));
			workCompleted.getProperties().setWorkLogList(list);
			return list;
		}
	}

}