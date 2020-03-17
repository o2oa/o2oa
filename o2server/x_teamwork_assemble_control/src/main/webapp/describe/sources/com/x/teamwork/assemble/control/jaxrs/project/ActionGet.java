package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.ProjectGroup;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Project project = null;
		ProjectDetail projectDetail = null;
		List<String> groupIds = null;
		List<ProjectGroup> groups = null;
		WrapOutControl control = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new ProjectFlagForQueryEmptyException();
			result.error( exception );
		}

		String cacheKey = ApplicationCache.concreteCacheKey( flag );
		Element element = projectCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {
			if (check) {
				try {
					project = projectQueryService.get(flag);
					if ( project == null) {
						check = false;
						Exception exception = new ProjectNotExistsException(flag);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectQueryException(e, "根据指定flag查询应用项目信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					wo = Wo.copier.copy( project );
					if( wo.getStarPersonList().contains( effectivePerson.getDistinguishedName() )) {
						wo.setStar( true );
					}					
					//查询项目详情
					projectDetail = projectQueryService.getDetail( project.getId() );
					if( projectDetail != null ) {
						wo.setDescription( projectDetail.getDescription() );
					}
					
					//查询项目组信息
					groupIds = projectGroupQueryService.listGroupIdByProject( project.getId() );
					groups = projectGroupQueryService.list( groupIds );
					wo.setGroups( groups );	
					
					control = new WrapOutControl();
					if( effectivePerson.isManager() || effectivePerson.getDistinguishedName().equalsIgnoreCase( project.getCreatorPerson() )) {
						control.setManageAble( true );
						control.setEditAble( true );
					}
					wo.setControl(control);
					projectCache.put(new Element(cacheKey, wo));
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new ProjectQueryException(e, "将查询出来的应用项目信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends WrapOutProject {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo( Project.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}