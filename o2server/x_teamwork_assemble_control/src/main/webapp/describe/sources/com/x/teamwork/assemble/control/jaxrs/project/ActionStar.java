package com.x.teamwork.assemble.control.jaxrs.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;

public class ActionStar extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStar.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Project project = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new ProjectFlagForQueryEmptyException();
			result.error( exception );
		}

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
		
		try {					
			projectPersistService.star( flag,  effectivePerson );
			
			// 更新缓存
			ApplicationCache.notify( Project.class );
			Wo wo = new Wo();
			wo.setId( project.getId() );
			result.setData( wo );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectPersistException(e, "项目信息保存时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}