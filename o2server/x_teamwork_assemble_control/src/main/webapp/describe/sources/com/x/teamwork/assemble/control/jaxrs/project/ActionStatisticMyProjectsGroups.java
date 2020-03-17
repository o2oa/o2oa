package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
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
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;

import net.sf.ehcache.Element;

public class ActionStatisticMyProjectsGroups extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStatisticMyProjectsGroups.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		List<String> projectIds = null;
		List<Project> projectList = null;
		List<ProjectGroup>  projectGroupList = null;
		List<WoGroup> woGroupList = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "ActionStatisticMyProjectsGroups", effectivePerson.getDistinguishedName() );
		Element element = projectCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {
			if (check) {
				try {
					//查询我参与的所有项目
					projectIds = projectQueryService.listAllViewableProjectIds( effectivePerson, 2000, new QueryFilter() );
					if( ListTools.isNotEmpty( projectIds )) {
						projectList = projectQueryService.list( projectIds );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectQueryException( e, "查询用户参与的所有项目信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					//查询我所有的项目组列表
					projectGroupList = projectGroupQueryService.listGroupByPerson( effectivePerson.getDistinguishedName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectQueryException( e, "查询用户所有项目组信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				if( ListTools.isNotEmpty( projectGroupList )) {
					woGroupList = WoGroup.copier.copy( projectGroupList );
					SortTools.asc( woGroupList, "createTime");
				}
			}			
			
			if (check) {
				if( ListTools.isNotEmpty( projectList )) {
					for(  Project project : projectList ) {
						woGroupList = checkGroup( project, woGroupList );
					}
				}
			}
			
			if (check) {
				try {
					wo = new Wo();
					if( ListTools.isNotEmpty( woGroupList )) {
						SortTools.asc( woGroupList, "createTime");
					}
					wo.setGroups( woGroupList );
					result.setData(wo);
					
					projectCache.put( new Element(cacheKey, wo) );					
				} catch (Exception e) {
					Exception exception = new ProjectQueryException(e, "将查询出来的应用项目信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	private List<WoGroup> checkGroup( Project project, List<WoGroup> woGroupList) throws Exception {
		if( ListTools.isEmpty( woGroupList )) {
			return null;
		}
		for( WoGroup woGroup : woGroupList ) {
			if( projectGroupQueryService.existsWithProjectAndGroup( woGroup.getId(), project.getId() ) ) {
				woGroup.addProjectCount(1);
				break;
			}
		}
		return woGroupList;
	}

	public static class Wo{
		
		@FieldDescribe("所有分组信息")
		private List<WoGroup> groups = null;
		
		public List<WoGroup> getGroups() {
			return groups;
		}

		public void setGroups(List<WoGroup> groups) {
			this.groups = groups;
		}		
	}
	
	public static class WoGroup extends ProjectGroup{
		
		@FieldDescribe("分组项目数量")
		private Integer projectCount = 0;
		
		public Integer getProjectCount() {
			return projectCount;
		}

		public void setProjectCount(Integer projectCount) {
			this.projectCount = projectCount;
		}

		public void addProjectCount( Integer count ) {
			if( this.projectCount == null ) {
				this.projectCount =0;
			}
			this.projectCount += count;
		}
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();
		
		static {
			Excludes.add("creatorPerson");
			Excludes.add("owner");
			Excludes.add("updateTime");
			Excludes.add("distributeFactor");
			Excludes.add("sequence");
		}
		
		static WrapCopier<ProjectGroup, WoGroup> copier = WrapCopierFactory.wo( ProjectGroup.class, WoGroup.class, null, Excludes);
	}
}