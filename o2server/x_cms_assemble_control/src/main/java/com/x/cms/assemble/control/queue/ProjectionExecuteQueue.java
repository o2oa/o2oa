package com.x.cms.assemble.control.queue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.factory.ProjectionFactory;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Projection;
import com.x.cms.core.entity.content.Data;
import com.x.query.core.entity.Item;

public class ProjectionExecuteQueue extends AbstractQueue<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectionExecuteQueue.class);

	private DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);

	@Override
	protected void execute(String id) throws Exception {
		LOGGER.debug("开始执行分类文档数据映射category:{}.", () -> id);
		CategoryInfo categoryInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			categoryInfo = emc.find(id, CategoryInfo.class);
			if (null == categoryInfo) {
				throw new ExceptionEntityNotExist(id, CategoryInfo.class);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		try {
			if (categoryInfo != null) {
				if (StringUtils.isNotBlank(categoryInfo.getProjection())
						&& XGsonBuilder.isJsonArray(categoryInfo.getProjection())) {
					List<Projection> projections = XGsonBuilder.instance().fromJson(categoryInfo.getProjection(),
							new TypeToken<List<Projection>>() {
							}.getType());
					this.doProjection(categoryInfo, projections);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}

		LOGGER.info("完成执行分类文档数据映射category：{}", id);
	}

	private void doProjection(CategoryInfo categoryInfo, final List<Projection> projections) throws Exception {
		List<String> docIdList;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			docIdList = business.getDocumentFactory().listByCategoryId(categoryInfo.getId(), null);
		}
		if (ListTools.isNotEmpty(docIdList)) {
			LOGGER.info("需要执行文档数据映射个数：{}", docIdList.size());
			int limit = 10;
			for (List<String> partJobs : ListTools.batch(docIdList, limit)) {
				List<CompletableFuture<Void>> futures = new TreeList<>();
				for (String docId : partJobs) {
					CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
						try {
							this.docProjection(docId, projections);
						} catch (Exception e) {
							LOGGER.warn("文档{}数据映射异常：{}", docId, e.getMessage());
							LOGGER.error(e);
						}
					}, ThisApplication.forkJoinPool());
					futures.add(future);
				}
				for (CompletableFuture<Void> future : futures) {
					try {
						future.get(300, TimeUnit.SECONDS);
					} catch (Exception e) {
						LOGGER.warn("执行文档数据映射任务异常：{}", e.getMessage());
					}
				}
				futures.clear();
			}
		}
	}

	private void docProjection(String docId, List<Projection> projections) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Data data = this.data(business, docId);
			Document document = emc.find(docId, Document.class);
			emc.beginTransaction(Document.class);
			ProjectionFactory.projectionDocument(projections, data, document);
			emc.commit();
		}
	}

	private Data data(Business business, String docId) throws Exception {
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
				docId, DataItem.itemCategory_FIELDNAME, ItemCategory.cms);
		if (items.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				return XGsonBuilder.convert(jsonElement, Data.class);
			} else {
				// 如果不是Object强制返回一个Map对象
				return new Data();
			}
		}
	}
}
