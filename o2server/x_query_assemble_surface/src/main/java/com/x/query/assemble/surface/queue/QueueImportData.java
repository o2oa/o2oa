package com.x.query.assemble.surface.queue;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.Applications;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_hotpic_assemble_control;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.cms.core.entity.CategoryInfo;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.ImportRecord;
import com.x.query.core.entity.ImportRecordItem;
import com.x.query.core.entity.schema.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据模板数据导入
 *
 */
public class QueueImportData extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(QueueImportData.class);

	private static Gson gson = XGsonBuilder.instance();

	public static final String PROCESS_STATUS_DRAFT = "draft";

	public void execute( String recordId ) {
		logger.info("开始数据模板数据导入：{}", recordId);
		try {
			ImportRecord record = null;
			ImportModel model = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				record = emc.find(recordId, ImportRecord.class);
				if(record == null){
					logger.warn("导入记录不存在：{}",recordId);
					return;
				}
				if(record.getStatus().equals(ImportRecord.STATUS_PROCESSING)){
					logger.warn("导入记录正在导入：{}",recordId);
					return;
				}
				model = emc.find(record.getModelId(), ImportModel.class);
				if(model == null){
					logger.warn("导入记录对应的导入模型不存在：{}",record.getModelId());
					return;
				}
				emc.beginTransaction(ImportRecord.class);
				record.setStatus(ImportRecord.STATUS_PROCESSING);
				emc.commit();
			}
			try {
				switch (model.getType()) {
					case ImportModel.TYPE_CMS:
						importCms(record, model);
						break;
					case ImportModel.TYPE_DYNAMIC_TABLE:
						importDynamicTable(record, model);
						break;
					case ImportModel.TYPE_PROCESSPLATFORM:
						importProcessPlatform(record, model);
						break;
				}
			} catch (Exception e) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					record = emc.find(recordId, ImportRecord.class);
					emc.beginTransaction(ImportRecord.class);
					record.setStatus(ImportRecord.STATUS_FAILED);
					emc.commit();
				}
				logger.warn("数据模板数据导入异常：{}", recordId);
				logger.error(e);
			}
		} catch (Exception e) {
			logger.warn("数据模板数据导入异常：{}", recordId);
			logger.error(e);
		}
		logger.info("完成数据模板数据导入：{}", recordId);
	}

	public void importCms(final ImportRecord record, final ImportModel model) throws Exception {
		JsonElement jsonElement = gson.fromJson(record.getData(), JsonElement.class);
		JsonObject jsonObject = gson.fromJson(model.getData(), JsonObject.class);
		String categoryId = jsonObject.getAsJsonObject("category").getAsJsonObject("id").getAsString();
		String documentType = jsonObject.getAsJsonObject("documentType").getAsString();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			CategoryInfo categoryInfo = emc.flag(categoryId, CategoryInfo.class);
			if (null == categoryInfo) {
				throw new ExceptionEntityNotExist(categoryId, CategoryInfo.class);
			}
		}
		final List<ImportRecordItem> itemList = new ArrayList<>();
		jsonElement.getAsJsonArray().forEach(o -> {
			JsonObject document = o.getAsJsonObject();
			document.addProperty("categoryId", categoryId);
			document.addProperty("documentType", documentType);
			document.addProperty("docStatus", "published");
			document.addProperty("isNotice", false);
			String title = document.getAsJsonObject("title").getAsString();
			ImportRecordItem item = new ImportRecordItem();
			item.setDocTitle(title);
			item.setDocType(model.getType());
			item.setRecordId(record.getId());
			item.setModelId(record.getModelId());
			item.setQuery(record.getQuery());
			item.setData(o.toString());
			try {
				WoId woId = ThisApplication.context().applications().putQuery(x_cms_assemble_control.class,
						Applications.joinQueryUri("document", "publish", "content"), document).getData(WoId.class);
				item.setDocId(woId.getId());
				item.setStatus(ImportRecordItem.STATUS_SUCCESS);
			} catch (Exception e) {
				item.setStatus(ImportRecordItem.STATUS_FAILED);
				item.setDistribution(e.getMessage());
			}
			itemList.add(item);
		});
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ImportRecord ir = emc.find(record.getId(), ImportRecord.class);
			boolean hasSuccess = false;
			boolean hasFailed = false;
			emc.beginTransaction(ImportRecord.class);
			emc.beginTransaction(ImportRecordItem.class);
			for (ImportRecordItem o : itemList) {
				if(ImportRecordItem.STATUS_FAILED.equals(o.getStatus())){
					hasFailed = true;
				}else{
					hasSuccess = true;
				}
				emc.persist(o, CheckPersistType.all);
			}
			String status = ImportRecord.STATUS_SUCCESS;
			if(hasFailed){
				if(hasSuccess){
					status = ImportRecord.STATUS_PART_SUCCESS;
				}else{
					status = ImportRecord.STATUS_FAILED;
				}
			}
			ir.setStatus(status);
			emc.commit();
		}
	}

	public void importDynamicTable(ImportRecord record, ImportModel model) throws Exception {
		JsonElement jsonElement = gson.fromJson(record.getData(), JsonElement.class);
		JsonObject jsonObject = gson.fromJson(model.getData(), JsonObject.class);
		String tableId = jsonObject.getAsJsonObject("dynamicTable").getAsJsonObject("id").getAsString();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ImportRecord ir = emc.find(record.getId(), ImportRecord.class);
			Table table = emc.flag(tableId, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(tableId, Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<JpaObject>) Class.forName(dynamicEntity.className());
			List<Object> os = new ArrayList<>();
			if (jsonElement.isJsonArray()) {
				jsonElement.getAsJsonArray().forEach(o -> {
					os.add(gson.fromJson(o, cls));
				});
			} else if (jsonElement.isJsonObject()) {
				os.add(gson.fromJson(jsonElement, cls));
			}
			emc.beginTransaction(ImportRecord.class);
			emc.beginTransaction(cls);
			for (Object o : os) {
				emc.persist((JpaObject) o, CheckPersistType.all);
			}
			ir.setStatus(ImportRecord.STATUS_SUCCESS);
			emc.commit();
		}
	}

	public void importProcessPlatform(ImportRecord record, ImportModel model) throws Exception {
		JsonElement jsonElement = gson.fromJson(record.getData(), JsonElement.class);
		JsonObject jsonObject = gson.fromJson(model.getData(), JsonObject.class);
		String processId = jsonObject.getAsJsonObject("process").getAsJsonObject("id").getAsString();
		String processStatus = jsonObject.getAsJsonObject("processStatus").getAsString();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Process process = emc.flag(processId, Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(processId, Process.class);
			}
		}
		final List<ImportRecordItem> itemList = new ArrayList<>();
		jsonElement.getAsJsonArray().forEach(o -> {
			JsonObject document = o.getAsJsonObject();
			String title = document.getAsJsonObject("title").getAsString();
			ImportRecordItem item = new ImportRecordItem();
			item.setDocTitle(title);
			item.setDocType(model.getType());
			item.setRecordId(record.getId());
			item.setModelId(record.getModelId());
			item.setQuery(record.getQuery());
			item.setData(o.toString());
			try {
				if(PROCESS_STATUS_DRAFT.equals(processStatus)) {
					List<WorkLog> workLogList = ThisApplication.context().applications().postQuery(x_processplatform_assemble_surface.class,
							Applications.joinQueryUri("work", "process", processId), document).getDataAsList(WorkLog.class);
					item.setDocId(workLogList.get(0).getWork());
					item.setStatus(ImportRecordItem.STATUS_SUCCESS);
				}else{
					WoId woId = ThisApplication.context().applications().putQuery(x_processplatform_assemble_surface.class,
							Applications.joinQueryUri("workcompleted", "process", processId), document).getData(WoId.class);
					item.setDocId(woId.getId());
					item.setStatus(ImportRecordItem.STATUS_SUCCESS);
				}
			} catch (Exception e) {
				item.setStatus(ImportRecordItem.STATUS_FAILED);
				item.setDistribution(e.getMessage());
			}
			itemList.add(item);
		});
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ImportRecord ir = emc.find(record.getId(), ImportRecord.class);
			boolean hasSuccess = false;
			boolean hasFailed = false;
			emc.beginTransaction(ImportRecord.class);
			emc.beginTransaction(ImportRecordItem.class);
			for (ImportRecordItem o : itemList) {
				if(ImportRecordItem.STATUS_FAILED.equals(o.getStatus())){
					hasFailed = true;
				}else{
					hasSuccess = true;
				}
				emc.persist(o, CheckPersistType.all);
			}
			String status = ImportRecord.STATUS_SUCCESS;
			if(hasFailed){
				if(hasSuccess){
					status = ImportRecord.STATUS_PART_SUCCESS;
				}else{
					status = ImportRecord.STATUS_FAILED;
				}
			}
			ir.setStatus(status);
			emc.commit();
		}
	}
}
