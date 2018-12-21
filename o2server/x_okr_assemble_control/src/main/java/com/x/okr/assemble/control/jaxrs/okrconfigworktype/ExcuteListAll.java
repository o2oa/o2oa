package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigListAll;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Element;

public class ExcuteListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ExcuteListAll.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<OkrConfigWorkType> okrConfigWorkTypeList = null;

		String cacheKey = catchNamePrefix + ".all";
		Element element = null;

		element = cache.get(cacheKey);
		if (element != null) {
			wraps = (List<Wo>) element.getObjectValue();
			result.setData(wraps);
		} else {
			try {
				okrConfigWorkTypeList = okrConfigWorkTypeService.listAll();
				if (okrConfigWorkTypeList != null && !okrConfigWorkTypeList.isEmpty()) {
					wraps = Wo.copier.copy(okrConfigWorkTypeList);
					cache.put(new Element(cacheKey, wraps));
					result.setData(wraps);
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkTypeConfigListAll(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends OkrConfigWorkType {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<OkrConfigWorkType, Wo> copier = WrapCopierFactory.wo(OkrConfigWorkType.class, Wo.class,
				null, JpaObject.FieldsInvisible);

		private Long centerCount = 0L;

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Long getCenterCount() {
			return centerCount;
		}

		public void setCenterCount(Long centerCount) {
			this.centerCount = centerCount;
		}
	}
}