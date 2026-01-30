package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectGroup;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		ProjectGroup projectGroup = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new ProjectGroupFlagForQueryEmptyException();
			result.error( exception );
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( id );
		Optional<?> optional = CacheManager.get(projectGroupCache, cacheKey);

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			result.setData( wo );
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					projectGroup = projectGroupQueryService.get( id );
					if ( projectGroup == null) {
						check = false;
						Exception exception = new ProjectGroupNotExistsException( id );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectGroupQueryException(e, "根据指定flag查询项目组信息对象时发生异常。id:" + id );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					wo = Wo.copier.copy( projectGroup );
					CacheManager.put(projectGroupCache,cacheKey,wo);
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new ProjectGroupQueryException(e, "将查询出来的项目组信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends ProjectGroup {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectGroup, Wo> copier = WrapCopierFactory.wo( ProjectGroup.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}