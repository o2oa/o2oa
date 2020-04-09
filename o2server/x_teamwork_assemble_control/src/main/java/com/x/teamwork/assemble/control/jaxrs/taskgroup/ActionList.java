package com.x.teamwork.assemble.control.jaxrs.taskgroup;

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
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.TaskGroup;

import net.sf.ehcache.Element;

public class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<TaskGroup> taskGroups = null;
		Project project = null;
		Boolean check = true;

		String cacheKey = ApplicationCache.concreteCacheKey( "ActionList.taskgroup", projectId, effectivePerson.getDistinguishedName() );
		Element element = taskGroupCache.get( cacheKey );
		
		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData( wos );
		} else {
			if (check) {
				try {
					project = projectQueryService.get( projectId );
					if ( project == null) {
						check = false;
						Exception exception = new ProjectNotExistsException( projectId );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskGroupQueryException(e, "根据指定ID查询应用项目信息对象时发生异常。ID:" + project);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					taskGroups = taskGroupQueryService.listGroupByPersonAndProject(effectivePerson, projectId );
					if( ListTools.isNotEmpty( taskGroups )) {
						wos = Wo.copier.copy( taskGroups );
						
						SortTools.asc( wos, "createTime");
						
						taskGroupCache.put(new Element(cacheKey, wos));
						result.setData(wos);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskGroupQueryException(e, "根据用户拥有的工作任务组信息列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends TaskGroup {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskGroup, Wo> copier = WrapCopierFactory.wo( TaskGroup.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}