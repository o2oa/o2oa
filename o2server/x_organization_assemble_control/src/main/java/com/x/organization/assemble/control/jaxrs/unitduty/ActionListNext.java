package com.x.organization.assemble.control.jaxrs.unitduty;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.UnitDuty;

import net.sf.ehcache.Element;

class ActionListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(), flag, count);
			Element element = business.cache().get(cacheKey);
			if (null != element && null != element.getObjectValue()) {
				Co co = (Co) element.getObjectValue();
				result.setData(co.getWos());
				result.setCount(co.getCount());
			} else {

				String id = EMPTY_SYMBOL;
				/** 如果不是空位标志位 */
				if (!StringUtils.equals(EMPTY_SYMBOL, flag)) {
					UnitDuty o = business.unitDuty().pick(flag);
					if (null == o) {
						throw new ExceptionUnitDutyNotExist(flag);
					}
					id = o.getId();
				}
				result = this.standardListNext(Wo.copier, id, count,  JpaObject.sequence_FIELDNAME, null, null, null, null, null, null,
						null, null, true, DESC);
				Co co = new Co(result.getData(), result.getCount());
				business.cache().put(new Element(cacheKey, co));
			}
			return result;
		}
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

	public static class Wo extends UnitDuty {

		private static final long serialVersionUID = -127291000673692614L;

		@FieldDescribe("排序号")
		private Long rank;

		static WrapCopier<UnitDuty, Wo> copier = WrapCopierFactory.wo(UnitDuty.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}