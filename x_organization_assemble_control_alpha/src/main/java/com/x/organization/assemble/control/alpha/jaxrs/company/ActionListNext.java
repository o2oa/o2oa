package com.x.organization.assemble.control.alpha.jaxrs.company;

import java.util.List;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.assemble.control.alpha.CacheFactory;
import com.x.organization.core.entity.Company;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class ActionListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), id, count);
		Ehcache cache = CacheFactory.getCompanyCache();
		Element element = cache.get(cacheKey);
		if (null != element && null != element.getObjectValue()) {
			Co co = (Co) element.getObjectValue();
			result.setData(co.getWos());
			result.setCount(co.getCount());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				result = this.standardListNext(Wo.copier, id, count, "sequence", null, null, null, null, null, null,
						null, true, DESC);
				result.getData().stream().forEach(o -> {
					try {
						o.setCompanySubDirectCount(business.company().countSubDirect(o.getId()));
						o.setDepartmentSubDirectCount(business.department().countSubDirectWithCompany(o.getId()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				Co co = new Co(result.getData(), result.getCount());
				cache.put(new Element(cacheKey, co));
			}
		}
		return result;
	}

	public static class Co extends GsonPropertyObject {

		public Co(List<Wo> wos, Long count) {
			this.wos = wos;
			this.count = count;
		}

		List<Wo> wos;
		Long count;

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

		public List<Wo> getWos() {
			return wos;
		}

		public void setWos(List<Wo> wos) {
			this.wos = wos;
		}
	}

	public static class Wo extends Company {

		private static final long serialVersionUID = -125007357898871894L;

		static WrapCopier<Company, Wo> copier = WrapCopierFactory.wo(Company.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		private Long companySubDirectCount;
		private Long departmentSubDirectCount;

		public Long getCompanySubDirectCount() {
			return companySubDirectCount;
		}

		public void setCompanySubDirectCount(Long companySubDirectCount) {
			this.companySubDirectCount = companySubDirectCount;
		}

		public Long getDepartmentSubDirectCount() {
			return departmentSubDirectCount;
		}

		public void setDepartmentSubDirectCount(Long departmentSubDirectCount) {
			this.departmentSubDirectCount = departmentSubDirectCount;
		}

	}
}