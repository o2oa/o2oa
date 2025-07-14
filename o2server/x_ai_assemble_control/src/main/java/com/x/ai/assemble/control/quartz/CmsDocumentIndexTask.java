package com.x.ai.assemble.control.quartz;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

/**
 * 定时索引文档到知识库
 *
 * @author sword
 */
public class CmsDocumentIndexTask extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(CmsDocumentIndexTask.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) {
		try {
			AiConfig config = Business.getConfig();
			LOGGER.info("定时索引文档到知识库-来源于内容管理应用：{}", StringUtils.join(config.getKnowledgeIndexAppList()));
			List<String> appIdList = config.getKnowledgeIndexAppList().stream().map(o -> StringUtils.substringBefore(o, "|")).collect(
					Collectors.toList());
			for (String appId : appIdList) {
				List<String> docIds = queryDocIds(appId);
				for (String docId : docIds) {
					ThisApplication.queueDocumentIndex.send(docId);
				}
			}
			LOGGER.info("定时索引文档到问答库-来源于内容管理应用：{}", StringUtils.join(config.getQuestionsIndexAppList()));
			appIdList = config.getQuestionsIndexAppList().stream().map(o -> StringUtils.substringBefore(o, "|")).collect(
					Collectors.toList());
			for (String appId : appIdList) {
				List<String> docIds = queryDocIds(appId);
				for (String docId : docIds) {
					ThisApplication.queueDocumentIndex.send(docId);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}


	private List<String> queryDocIds(String appId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get( Document.class );
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery( String.class );
			Root<Document> root = cq.from( Document.class );
			Predicate p = cb.equal(root.get( Document_.appId ), appId );
			p = cb.and(p, cb.equal(root.get( Document_.docStatus ), Document.DOC_STATUS_PUBLISH ));
			cq.select( root.get( Document_.id) ).where(p);
			return em.createQuery(cq).getResultList();
		}
	}

}
