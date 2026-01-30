package com.x.teamwork.assemble.control.jaxrs.list;

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
import com.x.teamwork.core.entity.TaskList;

public class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<TaskList> taskLists = null;
		Boolean check = true;

		Cache.CacheKey cacheKey = new Cache.CacheKey( "list.my", effectivePerson.getDistinguishedName(), projectId );
		Optional<?> optional = CacheManager.get(taskListCache, cacheKey);

		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
			result.setData( wos );
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					taskLists = taskListQueryService.listWithProject( effectivePerson.getDistinguishedName(), projectId );
					if( ListTools.isNotEmpty( taskLists )) {
						wos = Wo.copier.copy( taskLists );
						CacheManager.put(taskListCache,cacheKey,wos);
						result.setData(wos);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskListQueryException(e, "根据用户拥有的工作任务列表信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends TaskList {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskList, Wo> copier = WrapCopierFactory.wo( TaskList.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}