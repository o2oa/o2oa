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
import com.x.teamwork.core.entity.Priority;


public class ActionPriorityGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPriorityGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Priority priority = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new PriorityFlagForQueryEmptyException();
			result.error( exception );
		}

		if( Boolean.TRUE.equals( check ) ){
			try {
				priority = priorityQueryService.get( id );
				if ( priority == null) {
					check = false;
					Exception exception = new PriorityNotExistsException( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new PriorityQueryException(e, "根据指定flag查询优先级信息对象时发生异常。id:" + id );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {
				wo = Wo.copier.copy( priority );					
				result.setData(wo);
			} catch (Exception e) {
				Exception exception = new PriorityQueryException(e, "将查询出来的优先级信息对象转换为可输出的数据信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends Priority {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Priority, Wo> copier = WrapCopierFactory.wo( Priority.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}