package com.x.query.service.processing.schedule;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.BooleanUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.quartz.JobExecutionContext;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.index.State;
import com.x.query.service.processing.Business;
import com.x.query.service.processing.Solr;
import com.x.query.service.processing.ThisApplication;

public class HighFrequencyIndexWorkCompleted extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(HighFrequencyIndexWorkCompleted.class);

	private final DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);

	private final Gson gson = XGsonBuilder.instance();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {

		State state = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			state = emc.firstEqualAndEqual(State.class, State.NODE_FIELDNAME, Config.node(), State.FREQUENCY_FIELDNAME,
					State.FREQUENCY_HIGH);
			List<String> ids = this.list(business, state);
			if (!ids.isEmpty()) {
				Solr solr = ThisApplication.solr();
				if (null != solr) {
					index(solr, ids);
				}
			}
		}

	}

	private List<String> list(Business business, State state) throws Exception {
		EntityManager em = business.entityManagerContainer().get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = null;
		if (null != state) {
			p = cb.notEqual(root.get(WorkCompleted_.id), state.getLatestId());
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(JpaObject_.createTime), state.getLatestCreateTime()));
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(JpaObject_.sequence), state.getLatestSequence()));
		} else {
			p = cb.conjunction();
		}
		cq.select(root.get(WorkCompleted_.id)).where(p);
		return em.createQuery(cq).setMaxResults(100).getResultList();
	}

	private void index(Solr solr, List<String> ids) {
		for (String id : ids) {
			try {
				index(solr, id);
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	private void index(Solr solr, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WorkCompleted workCompleted = emc.find(id, WorkCompleted.class);
			if (null != workCompleted) {
				EmbeddedSolrServer server = solr.get("processPlatform", id);
				if (null != server) {
					add(business, server, workCompleted);
				}
			}
		}
	}

	private void add(Business business, EmbeddedSolrServer server, WorkCompleted workCompleted) throws Exception {
		JsonObject json = XGsonBuilder.convert(data(business, workCompleted), JsonObject.class);
		json.addProperty(WorkCompleted.title_FIELDNAME, workCompleted.getTitle());
		json.addProperty(WorkCompleted.job_FIELDNAME, workCompleted.getJob());
		json.addProperty(WorkCompleted.createTime_FIELDNAME, DateTools.format(workCompleted.getCompletedTime()));
		json.addProperty(WorkCompleted.updateTime_FIELDNAME, DateTools.format(workCompleted.getUpdateTime()));
		json.addProperty(WorkCompleted.job_FIELDNAME, workCompleted.getJob());
		json.addProperty(WorkCompleted.id_FIELDNAME, workCompleted.getId());
		json.addProperty(WorkCompleted.application_FIELDNAME, workCompleted.getApplication());
		json.addProperty(WorkCompleted.process_FIELDNAME, workCompleted.getProcess());
		json.addProperty("className", WorkCompleted.class.getName());

		server.add(document);
		server.commit();

	}

	private Data data(Business business, WorkCompleted workCompleted) throws Exception {

		Data data = null;
		if (BooleanUtils.isTrue(workCompleted.getMerged())) {
			data = workCompleted.getProperties().getData();
		} else {
			List<Item> items = business.entityManagerContainer().listEqualAndEqual(Item.class,
					DataItem.bundle_FIELDNAME, workCompleted.getJob(), DataItem.itemCategory_FIELDNAME,
					ItemCategory.pp);
			JsonElement jsonElement = converter.assemble(items);
			if (jsonElement.isJsonObject()) {
				data = gson.fromJson(jsonElement, Data.class);
			} else {
				data = new Data();
			}
		}
		data.removeWork();
		data.removeAttachmentList();
		return data;
	}

	private SolrInputDocument concrete(WorkCompleted workCompleted, JsonObject jsonObject) {
		SolrInputDocument document = new SolrInputDocument();
		jsonObject.entrySet().forEach(en -> {
			set(en.getKey(), en.getValue(), document);
		});
		document.setField(WorkCompleted.title_FIELDNAME, workCompleted.getTitle());
		return document;
	}

	private void set(String property, JsonElement jsonElement, SolrInputDocument document) {
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				document.setField(property, jsonPrimitive.getAsBoolean());
			} else if (jsonPrimitive.isNumber()) {
				document.setField(property, jsonPrimitive.getAsFloat());
			} else {
				document.setField(property, jsonPrimitive.getAsString());
			}
		}
	}

}