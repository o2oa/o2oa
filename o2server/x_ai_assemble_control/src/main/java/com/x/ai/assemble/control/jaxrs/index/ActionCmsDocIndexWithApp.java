package com.x.ai.assemble.control.jaxrs.index;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Document_;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCmsDocIndexWithApp extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String appId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if(effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		AiConfig aiConfig = Business.getConfig();
		if (BooleanUtils.isNotTrue(aiConfig.getO2AiEnable())
				|| StringUtils.isBlank(aiConfig.getO2AiBaseUrl())
				|| StringUtils.isBlank(aiConfig.getO2AiToken())) {
			throw new ExceptionCustom("请先启用o2智能体并进行相关配置.");
		}
		List<String> docIds = listByAppId(appId);
		for (String docId : docIds) {
			ThisApplication.queueDocumentIndex.send(docId);
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private List<String> listByAppId(String appId) throws Exception {
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

	public static class Wo extends WrapBoolean {

	}



}
