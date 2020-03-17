package com.x.organization.assemble.express.jaxrs.empower;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.ThisApplication;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.Empower_;
import com.x.organization.core.entity.accredit.Filter;

import net.sf.ehcache.Element;

class ActionListWithIdentityObject extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithIdentityObject.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = this.convert(business, wi);
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("应用")
		private String application;

		@FieldDescribe("流程")
		private String process;

		@FieldDescribe("工作")
		private String work;

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

		public String getWork() {
			return work;
		}

		public void setWork(String work) {
			this.work = work;
		}

	}

	public static class Wo extends Empower {

		private static final long serialVersionUID = 1559687446726204650L;

		public Wo() {

		}

		public Wo(String fromIdentity, String toIdentity) {
			this.setFromIdentity(fromIdentity);
			this.setToIdentity(toIdentity);
		}

	}

	private List<Wo> convert(Business business, Wi wi) throws Exception {

		List<Wo> wos = new ArrayList<>();

		Map<String, List<Empower>> map = this.list(business, wi).stream()
				.collect(Collectors.groupingBy(o -> o.getFromIdentity()));

		for (String str : wi.getIdentityList()) {

			List<Empower> list = map.get(str);

			if (ListTools.isNotEmpty(list)) {
				list.sort(new Comparator<Empower>() {
					public int compare(Empower o1, Empower o2) {
						if (StringUtils.equals(Empower.TYPE_FILTER, o1.getType())) {
							return -1;
						} else if (StringUtils.equals(Empower.TYPE_FILTER, o2.getType())) {
							return 1;
						} else if (StringUtils.equals(Empower.TYPE_PROCESS, o1.getType())) {
							return -1;
						} else if (StringUtils.equals(Empower.TYPE_PROCESS, o2.getType())) {
							return 1;
						} else if (StringUtils.equals(Empower.TYPE_APPLICATION, o1.getType())) {
							return -1;
						} else if (StringUtils.equals(Empower.TYPE_APPLICATION, o2.getType())) {
							return 1;
						} else {
							return 0;
						}
					}
				});
				Empower empower = this.pick(business, list, wi.getWork());
				if (null != empower) {
					Wo wo = new Wo(str, str);
					wo.setToIdentity(list.get(0).getToIdentity());
					wos.add(wo);
				}
			}
		}
		return wos;
	}

	private Empower pick(Business business, List<Empower> list, String work) throws Exception {
		for (Empower empower : list) {
			if (StringUtils.equals(Empower.TYPE_FILTER, empower.getType())
					&& StringUtils.isNotEmpty(empower.getFilterListData())) {
				List<Filter> filters = gson.fromJson(empower.getFilterListData(), Filter.LISTTYPE);
				Filter filter = filters.get(0);
				ActionResponse response = ThisApplication.context().applications().getQuery(
						x_processplatform_assemble_surface.class, Applications.joinQueryUri("data", "work", work) + "/"
								+ StringUtils.replace(filter.path, ".", "/"));
				if (null != response.getData()) {
					switch (Objects.toString(filter.formatType, "")) {
					default:
						if (StringUtils.equals(filter.value, response.getData().getAsString())) {
							return empower;
						}
						break;
					}
				} else {
					return empower;
				}
			}
		}
		return list.get(0);
	}

	private List<Empower> list(Business business, Wi wi) throws Exception {

		List<Identity> identities = business.identity().pick(wi.getIdentityList());
		List<String> names = ListTools.extractProperty(identities, JpaObject.DISTINGUISHEDNAME, String.class, true,
				true);
		EntityManager em = business.entityManagerContainer().get(Empower.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Empower> cq = cb.createQuery(Empower.class);
		Root<Empower> root = cq.from(Empower.class);
		Predicate p = cb.or(cb.equal(root.get(Empower_.type), Empower.TYPE_ALL),
				cb.and(cb.equal(root.get(Empower_.type), Empower.TYPE_APPLICATION),
						cb.equal(root.get(Empower_.application), wi.getApplication())),
				cb.and(cb.equal(root.get(Empower_.type), Empower.TYPE_PROCESS),
						cb.equal(root.get(Empower_.process), wi.getProcess())),
				cb.and(cb.equal(root.get(Empower_.type), Empower.TYPE_FILTER),
						cb.equal(root.get(Empower_.process), wi.getProcess())));
		p = cb.and(p, root.get(Empower_.fromIdentity).in(names));
		// p = cb.and(p, cb.isMember(root.get(Empower_.fromIdentity),
		// cb.literal(names)));
		p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
		p = cb.and(p, cb.lessThan(root.get(Empower_.startTime), new Date()),
				cb.greaterThan(root.get(Empower_.completedTime), new Date()));
		return em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();

	}

}