package com.x.organization.assemble.express.jaxrs.trust;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.accredit.Trust;
import com.x.organization.core.entity.accredit.Trust_;

import net.sf.ehcache.Element;

class ActionListWithIdentityObject extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithIdentityObject.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), wi.getApplication(), wi.getProcess(),
					StringUtils.join(wi.getIdentityList(), ","));
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.execute(business, wi);
				cache.put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private String application;

		@FieldDescribe("流程")
		private String process;

		@FieldDescribe("身份")
		private List<String> identityList = new ArrayList<>();

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getProcess() {
			return process;
		}

		public void setProcess(String process) {
			this.process = process;
		}

	}

	public static class Wo extends com.x.base.core.project.organization.Trust {

		public Wo() {

		}

		public Wo(String fromIdentity, String toIdentity) {
			this.setFromIdentity(fromIdentity);
			this.setToIdentity(toIdentity);
		}

	}

	private List<Trust> list(Business business, Wi wi) throws Exception {

		List<Identity> identities = business.identity().pick(wi.getIdentityList());
		List<String> ids = ListTools.extractProperty(identities, JpaObject.id_FIELDNAME, String.class, true, true);

		EntityManager em = business.entityManagerContainer().get(Trust.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Trust> cq = cb.createQuery(Trust.class);
		Root<Trust> root = cq.from(Trust.class);
		Predicate p = cb.equal(root.get(Trust_.whole), true);
		if (StringUtils.isNotEmpty(wi.getApplication())) {
			p = cb.and(p,
					cb.and(cb.notEqual(root.get(Trust_.whole), true),
							cb.equal(root.get(Trust_.application), wi.getApplication()),
							cb.or(cb.equal(root.get(Trust_.process), ""), cb.isNull(root.get(Trust_.process)))));
		}
		if (StringUtils.isNotEmpty(wi.getProcess())) {
			p = cb.and(p, cb.and(cb.notEqual(root.get(Trust_.whole), true),
					cb.equal(root.get(Trust_.process), wi.getProcess())));
		}
		cb.and(p, cb.isMember(root.get(Trust_.fromIdentity), cb.literal(ids)));

		return em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
	}

	private List<Wo> execute(Business business, Wi wi) throws Exception {

		List<Wo> wos = new ArrayList<>();
		this.list(business, wi).stream().collect(Collectors.groupingBy(o -> o.getFromIdentity())).entrySet().stream()
				.forEach(o -> {
					out: for (;;) {
						for (Trust t : o.getValue()) {
							if ((BooleanUtils.isNotTrue(t.getWhole())) && StringUtils.isNotEmpty(t.getProcess())) {
								Wo wo = new Wo(t.getFromIdentity(), t.getToIdentity());
								wos.add(wo);
								break out;
							}
						}
						for (Trust t : o.getValue()) {
							if ((BooleanUtils.isNotTrue(t.getWhole())) && StringUtils.isNotEmpty(t.getApplication())) {
								Wo wo = new Wo(t.getFromIdentity(), t.getToIdentity());
								wos.add(wo);
								break out;
							}
						}
						for (Trust t : o.getValue()) {
							if (BooleanUtils.isTrue(t.getWhole())) {
								Wo wo = new Wo(t.getFromIdentity(), t.getToIdentity());
								wos.add(wo);
								break out;
							}
						}
						break;
					}
				});
		return wos;
	}

}