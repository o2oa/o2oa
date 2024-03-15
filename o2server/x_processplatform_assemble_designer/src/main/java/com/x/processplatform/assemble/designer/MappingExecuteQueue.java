package com.x.processplatform.assemble.designer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Mapping;
import com.x.processplatform.core.entity.element.util.MappingFactory;
import com.x.query.core.entity.Item;

public class MappingExecuteQueue extends AbstractQueue<String> {

	private static Logger logger = LoggerFactory.getLogger(MappingExecuteQueue.class);

	private DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);

	@Override
	protected void execute(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Mapping mapping = emc.find(id, Mapping.class);
			if (null == mapping) {
				throw new ExceptionEntityNotExist(id, Mapping.class);
			}
			if (BooleanUtils.isTrue(mapping.getEnable())) {
				this.workCompleted(business, mapping);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void workCompleted(Business business, Mapping mapping) throws Exception {
		String sequence = "";
		List<WorkCompleted> os = new ArrayList<>();
		Data data = null;
		do {
			if (StringUtils.isNotEmpty(mapping.getProcess())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.process_FIELDNAME, mapping.getProcess(), 100, sequence);
			} else if (StringUtils.isNotEmpty(mapping.getApplication())) {
				os = business.entityManagerContainer().listEqualAndSequenceAfter(WorkCompleted.class,
						WorkCompleted.application_FIELDNAME, mapping.getApplication(), 100, sequence);
			} else {
				os = new ArrayList<>();
			}
			if (!os.isEmpty()) {
				Class<? extends JpaObject> cls = (Class<? extends JpaObject>) Class
						.forName(DynamicEntity.CLASS_PACKAGE + "." + mapping.getTableName());
				business.entityManagerContainer().beginTransaction(cls);
				for (WorkCompleted o : os) {
					sequence = o.getSequence();
					data = this.data(business, o);
					JpaObject jpaObject = business.entityManagerContainer().find(o.getJob(), cls);
					if (null == jpaObject) {
						jpaObject = (JpaObject) cls.newInstance();
						jpaObject.setId(o.getJob());
						business.entityManagerContainer().persist(jpaObject, CheckPersistType.all);
					}
					MappingFactory.mapping(mapping, o, data, jpaObject);
				}
				business.entityManagerContainer().commit();
			}
		} while (!os.isEmpty());
	}

	private Data data(Business business, WorkCompleted workCompleted) throws Exception {
		if (BooleanUtils.isTrue(workCompleted.getMerged()) && (null != workCompleted.getData())) {
			return workCompleted.getData();
		}
		List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class, Item.bundle_FIELDNAME,
				workCompleted.getJob(), Item.itemCategory_FIELDNAME, ItemCategory.pp);
		if (items.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				return XGsonBuilder.convert(jsonElement, Data.class);
			} else {
				/* 如果不是Object强制返回一个Map对象 */
				return new Data();
			}
		}
	}
}