package com.x.teamwork.assemble.control.jaxrs.global;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.ProjectConfig;

public class ActionProjectConfigGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionProjectConfigGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		ProjectConfig projectConfig = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new PriorityFlagForQueryEmptyException();
			result.error( exception );
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				projectConfig = projectConfigQueryService.get( id );
				if ( projectConfig == null) {
					check = false;
					Exception exception = new ProjectConfigNotExistsException( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectConfigQueryException(e, "根据指定flag查询项目配置信息对象时发生异常。id:" + id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {
				wo = Wo.copier.copy( projectConfig );					
				result.setData(wo);
			} catch (Exception e) {
				Exception exception = new ProjectConfigQueryException(e, "将查询出来的项目配置信息对象转换为可输出的数据信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends ProjectConfig {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectConfig, Wo> copier = WrapCopierFactory.wo( ProjectConfig.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}