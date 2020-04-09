package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

import net.sf.ehcache.Element;

public class ActionListWithProject extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithProject.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<ProjectExtFieldRele> projectExtFieldReles = null;
		Boolean check = true;

		String cacheKey = ApplicationCache.concreteCacheKey( "ActionList", projectId, effectivePerson.getDistinguishedName() );
		Element element = projectExtFieldReleCache.get( cacheKey );
		
		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData( wos );
		} else {
			if (check) {
				try {
					projectExtFieldReles = projectExtFieldReleQueryService.listReleWithProject(projectId);
					if( ListTools.isEmpty( projectExtFieldReles )) {
						projectExtFieldReles = new ArrayList<>();
					}
					wos = Wo.copier.copy( projectExtFieldReles );						
					SortTools.asc( wos, "createTime");						
					projectExtFieldReleCache.put(new Element(cacheKey, wos));
					result.setData(wos);	
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectExtFieldReleQueryException(e, "根据用户拥有的项目扩展属性关联信息列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends ProjectExtFieldRele {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectExtFieldRele, Wo> copier = WrapCopierFactory.wo( ProjectExtFieldRele.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}