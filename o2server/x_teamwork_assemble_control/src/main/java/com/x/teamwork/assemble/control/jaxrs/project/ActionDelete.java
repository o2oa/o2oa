package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectGroup;
import com.x.teamwork.core.entity.Task;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Project project = null;
		Boolean check = true;
		Wo wo = new Wo();

		if ( StringUtils.isEmpty( projectId ) ) {
			check = false;
			Exception exception = new ProjectFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				project = projectQueryService.get(projectId);
				if ( project == null) {
					check = false;
					Exception exception = new ProjectNotExistsException(projectId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectQueryException(e, "根据指定flag查询项目信息对象时发生异常。projectId:" + projectId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				projectPersistService.delete( projectId, effectivePerson );				
				// 更新缓存
				ApplicationCache.notify( ProjectGroup.class );
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( Project.class );
				
				wo.setId( project.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectQueryException(e, "根据指定flag删除项目信息对象时发生异常。projectId:" + projectId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		
		
		if (check) {
			try {					
				new BatchOperationPersistService().addOperation( 
						BatchOperationProcessService.OPT_OBJ_PROJECT, 
						BatchOperationProcessService.OPT_TYPE_DELETE,  projectId,  projectId, "刷新文档权限：ID=" +  projectId );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		
		if (check) {
			try {					
				Dynamic dynamic = dynamicPersistService.projectDeleteDynamic( project, effectivePerson);
				if( dynamic != null ) {
					List<WoDynamic> dynamics = new ArrayList<>();
					dynamics.add( WoDynamic.copier.copy( dynamic ) );
					if( wo != null ) {
						wo.setDynamics(dynamics);
					}
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {
		
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
		
	}
	
	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}		
	}
}