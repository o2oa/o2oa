package com.x.cms.assemble.control.timertask;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobExecutionContext;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import com.x.cms.core.entity.content.Data;
import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.entity.query.DocumentNotify;

/**
 * 定时发布待发布文档
 * 
 * @author sword
 */
public class PublishWaitDocumentTask extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(PublishWaitDocumentTask.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			List<String> ids = queryWaitPublishDocIds();
			if (ListTools.isNotEmpty(ids)) {
				LOGGER.info("开始定时发布文档数量{}.", ids.size());
				ids.stream().forEach(this::publishDocument);
				LOGGER.info("结束定时发布文档数量{}.", ids.size());
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void publishDocument(String id) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Document document = emc.find(id, Document.class);
			emc.beginTransaction(Document.class);
			document.setDocStatus(Document.DOC_STATUS_PUBLISH);
			document.setPublishTime(new Date());
			DocumentNotify documentNotify = document.getProperties().getDocumentNotify();
			document.getProperties().setDocumentNotify(null);
			emc.commit();
			DocumentDataHelper documentDataHelper = new DocumentDataHelper(emc, document);
			Data data = documentDataHelper.get();
			data.setDocument(document);
			documentDataHelper.update(data);
			emc.commit();
			if (documentNotify != null) {
				documentNotify.setDocumentId(id);
				ThisApplication.queueSendDocumentNotify.send(documentNotify);
			}
			LOGGER.info("完成发布文档：{}", document.getTitle());
		} catch (Exception e) {
			LOGGER.warn("发布文档：{}，异常：{}", id, e.getMessage());
		}
	}

	private List<String> queryWaitPublishDocIds() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Document.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<Document> root = cq.from(Document.class);
			Predicate p = cb.lessThan(root.get(Document_.publishTime), new Date());
			p = cb.and(p, cb.equal(root.get(Document_.docStatus), DocumentStatus.WAIT_PUBLISH.getValue()));
			cq.select(root.get(Document_.id));
			cq.where(p).orderBy(cb.asc(root.get(JpaObject_.createTime)));
			return em.createQuery(cq).getResultList();
		}
	}

}
