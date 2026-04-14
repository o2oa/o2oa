package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.CustomExtFieldRele;

public class ActionListWithCorrelation extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithCorrelation.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String correlationId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<CustomExtFieldRele> customExtFieldReles = null;
		Boolean check = true;

		Cache.CacheKey cacheKey = new Cache.CacheKey( "ActionListWithCorrelation", correlationId, effectivePerson.getDistinguishedName() );
		Optional<?> optional = CacheManager.get(customExtFieldReleCache, cacheKey);
		
		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
			result.setData( wos );
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					customExtFieldReles = customExtFieldReleQueryService.listReleWithCorrelation(correlationId);
					if( ListTools.isEmpty( customExtFieldReles )) {
						customExtFieldReles = new ArrayList<>();
					}
					wos = Wo.copier.copy( customExtFieldReles );						
					SortTools.asc( wos, "createTime");
					CacheManager.put(customExtFieldReleCache,cacheKey,wos);
					result.setData(wos);	
				} catch (Exception e) {
					check = false;
					Exception exception = new CustomExtFieldReleQueryException(e, "根据关联ID查询对应的扩展属性信息列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends CustomExtFieldRele {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<CustomExtFieldRele, Wo> copier = WrapCopierFactory.wo( CustomExtFieldRele.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}